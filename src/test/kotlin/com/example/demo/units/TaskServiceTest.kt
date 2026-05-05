package com.example.demo.units

import com.example.demo.dto.mapper.toResponse
import com.example.demo.dto.request.TaskRequest
import com.example.demo.dto.response.TaskResponse
import com.example.demo.repository.TaskRepository
import com.example.demo.repository.entity.TaskEntity
import com.example.demo.repository.entity.UserProgressEntity
import com.example.demo.service.TaskService
import com.example.demo.utils.ProgressUpdater
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.util.Optional
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TaskServiceTest {
    private val taskRepository = mockk<TaskRepository>(relaxed = true)
    private val progressUpdater = mockk<ProgressUpdater>(relaxed = true)
    private val service = TaskService(taskRepository, progressUpdater)

    private val userId = UUID.randomUUID()
    private val taskId = UUID.randomUUID()

    // Вспомогательная функция для создания реального TaskEntity
    private fun createEntity(
        id: UUID = taskId,
        userId: UUID = this.userId,
        text: String = "task",
        description: String? = null,
        completed: Boolean = false,
    ) = TaskEntity(
        id = id,
        userId = userId,
        text = text,
        description = description,
        completed = completed,
        date = LocalDate.now(),
    )

    // Вспомогательная функция для создания реального TaskResponse
    private fun createResponse(
        id: UUID = taskId,
        userId: UUID = this.userId,
        text: String = "task",
        description: String? = null,
        completed: Boolean = false,
    ) = TaskResponse(
        id = id,
        userId = userId,
        text = text,
        description = description,
        completed = completed,
        date = LocalDate.now(),
    )

    @Test
    fun `save should persist task and return response`() {
        val request = TaskRequest(userId = userId, text = "new task", completed = false)
        val savedEntity = createEntity(text = "new task")
        val expectedResponse = createResponse(text = "new task")

        // Мокаем только репозиторий
        every { taskRepository.save(any<TaskEntity>()) } returns savedEntity

        val result = service.save(request)
        assertEquals(expectedResponse.id, result.id)
        assertEquals(expectedResponse.text, result.text)
        verify { taskRepository.save(any<TaskEntity>()) }
    }

    @Test
    fun `findByUserIdAndDate should return list of responses`() {
        val date = LocalDate.now()
        val entity1 = createEntity(id = UUID.randomUUID(), text = "t1")
        val entity2 = createEntity(id = UUID.randomUUID(), text = "t2")

        every { taskRepository.findByUserIdAndDate(userId, date) } returns listOf(entity1, entity2)

        val result = service.findByUserIdAndDate(userId, date)
        assertEquals(2, result.size)
        assertEquals("t1", result[0].text)
        assertEquals("t2", result[1].text)
    }

    @Test
    fun `findById should return response when task exists`() {
        val entity = createEntity()
        every { taskRepository.findById(taskId) } returns Optional.of(entity)

        val result = service.findById(taskId)
        assertEquals(entity.id, result.id)
        assertEquals(entity.text, result.text)
    }

    @Test
    fun `findById should throw when task not found`() {
        every { taskRepository.findById(taskId) } returns Optional.empty()
        assertThrows<NoSuchElementException> { service.findById(taskId) }
    }

    @Test
    fun `findAllByUser should return list of responses`() {
        val entity1 = createEntity(id = UUID.randomUUID(), text = "t1")
        val entity2 = createEntity(id = UUID.randomUUID(), text = "t2")

        every { taskRepository.findAllByUserId(userId) } returns listOf(entity1, entity2)

        val result = service.findAllByUser(userId)
        assertEquals(2, result.size)
    }

    @Test
    fun `delete should call repository delete`() {
        every { taskRepository.deleteById(taskId) } just Runs
        service.delete(taskId)
        verify { taskRepository.deleteById(taskId) }
    }

    @Test
    fun `update should throw when task not found`() {
        val request = TaskRequest(id = taskId, userId = userId, text = "any")
        every { taskRepository.findById(taskId) } returns Optional.empty()
        assertThrows<NoSuchElementException> { service.update(request) }
    }

    @Test
    fun `update should not call progressUpdater when completed unchanged`() {
        val existing = createEntity(completed = true)
        val request = TaskRequest(id = taskId, userId = userId, text = "new", completed = true)

        every { taskRepository.findById(taskId) } returns Optional.of(existing)
        every { taskRepository.save(existing) } returns existing

        service.update(request)
        verify(exactly = 0) { progressUpdater.updateProgress(any(), any()) }
    }

    @Test
    fun `update should increment completedTasks when task becomes completed`() {
        val existing = createEntity(completed = false)
        val request = TaskRequest(id = taskId, userId = userId, text = "new", completed = true)

        every { taskRepository.findById(taskId) } returns Optional.of(existing)
        every { taskRepository.save(existing) } returns existing

        val updateSlot = slot<(UserProgressEntity) -> Unit>()
        every { progressUpdater.updateProgress(eq(userId), capture(updateSlot)) } just Runs

        service.update(request)

        assertTrue(updateSlot.isCaptured)
        val progress = UserProgressEntity(userId = userId, completedTasks = 5)
        updateSlot.captured.invoke(progress)
        assertEquals(6, progress.completedTasks)
    }

    @Test
    fun `update should decrement completedTasks when task becomes incomplete`() {
        val existing = TaskEntity(id = taskId, userId = userId, text = "old", completed = true)
        val request = TaskRequest(id = taskId, userId = userId, text = "new", completed = false)

        every { taskRepository.findById(taskId) } returns Optional.of(existing)
        every { taskRepository.save(existing) } returns existing

        val updateSlot = slot<(UserProgressEntity) -> Unit>()
        every { progressUpdater.updateProgress(eq(userId), capture(updateSlot)) } just Runs

        service.update(request)

        assertTrue(updateSlot.isCaptured)
        val progress = UserProgressEntity(userId = userId, completedTasks = 5)
        updateSlot.captured.invoke(progress)
        assertEquals(4, progress.completedTasks)
    }
}
