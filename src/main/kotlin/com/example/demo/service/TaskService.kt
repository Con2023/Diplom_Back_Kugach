package com.example.demo.service

import com.example.demo.dto.mapper.toEntity
import com.example.demo.dto.mapper.toResponse
import com.example.demo.dto.request.TaskRequest
import com.example.demo.dto.response.TaskResponse
import com.example.demo.repository.TaskRepository
import com.example.demo.utils.ProgressUpdater
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.UUID

@Service
class TaskService(
    private val taskRepository: TaskRepository,
    private val progressUpdater: ProgressUpdater,
) {
    @Transactional
    fun save(request: TaskRequest): TaskResponse = taskRepository.save(request.toEntity()).toResponse()

    @Transactional
    fun findByUserIdAndDate(
        userId: UUID,
        date: LocalDate,
    ): List<TaskResponse> = taskRepository.findByUserIdAndDate(userId, date).map { it.toResponse() }

    @Transactional(readOnly = true)
    fun findById(taskId: UUID): TaskResponse =
        taskRepository
            .findById(taskId)
            .orElseThrow { NoSuchElementException("Task not found") }
            .toResponse()

    @Transactional(readOnly = true)
    fun findAllByUser(userId: UUID): List<TaskResponse> = taskRepository.findAllByUserId(userId).map { it.toResponse() }

    @Transactional
    fun update(request: TaskRequest): TaskResponse {
        val existing =
            taskRepository
                .findById(request.id!!)
                .orElseThrow { NoSuchElementException("Task not found") }

        val wasCompleted = existing.completed
        existing.text = request.text
        existing.description = request.description
        existing.completed = request.completed
        val updated = taskRepository.save(existing)

        if (updated.completed != wasCompleted) {
            progressUpdater.updateProgress(existing.userId) { progress ->
                if (updated.completed) {
                    progress.completedTasks += 1
                } else {
                    progress.completedTasks = maxOf(0, progress.completedTasks - 1)
                }
            }
        }

        return updated.toResponse()
    }

    @Transactional
    fun delete(taskId: UUID) {
        taskRepository.deleteById(taskId)
    }
}
