package com.example.demo.service

import com.example.demo.dto.mapper.toEntity
import com.example.demo.dto.mapper.toResponse
import com.example.demo.dto.request.GoalRequest
import com.example.demo.dto.response.GoalResponse
import com.example.demo.repository.GoalRepository
import com.example.demo.repository.entity.TaskEntity
import com.example.demo.utils.ProgressUpdater
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GoalService(
    private val goalRepository: GoalRepository,
    private val progressUpdater: ProgressUpdater,
) {
    @Transactional
    fun save(request: GoalRequest): GoalResponse = goalRepository.save(request.toEntity()).toResponse()

    @Transactional
    fun findById(goalId: UUID): GoalResponse =
        goalRepository
            .findById(goalId)
            .orElseThrow { NoSuchElementException("Goal not found") }
            .toResponse()

    @Transactional
    fun findAllByUser(userId: UUID): List<GoalResponse> = goalRepository.findAllByUserId(userId).map { it.toResponse() }

    @Transactional
    fun update(request: GoalRequest): GoalResponse {
        val existing =
            goalRepository
                .findById(request.id!!)
                .orElseThrow { NoSuchElementException("Goal not found") }

        val wasAllCompleted = existing.tasks.all { it.completed }

        existing.text = request.text
        existing.category = request.category
        existing.priority = request.priority
        existing.deadline = request.deadline
        existing.tasks.clear()

        val newTasks =
            request.tasks.map { taskReq ->
                TaskEntity(
                    id = taskReq.id,
                    userId = existing.userId,
                    text = taskReq.text,
                    description = taskReq.description,
                    completed = taskReq.completed,
                    goal = existing,
                )
            }
        existing.tasks.addAll(newTasks)

        val updated = goalRepository.save(existing)
        val isAllCompletedNow = updated.tasks.all { it.completed }

        if (isAllCompletedNow && !wasAllCompleted) {
            progressUpdater.updateProgress(existing.userId) { progress ->
                progress.completedGoals += 1
            }
        } else if (!isAllCompletedNow && wasAllCompleted) {
            progressUpdater.updateProgress(existing.userId) { progress ->
                progress.completedGoals = maxOf(0, progress.completedGoals - 1)
            }
        }

        return updated.toResponse()
    }

    @Transactional
    fun delete(goalId: UUID) {
        goalRepository.deleteById(goalId)
    }
}
