package com.example.demo.units

import com.example.demo.dto.mapper.toResponse
import com.example.demo.dto.request.GoalRequest
import com.example.demo.dto.request.TaskRequest
import com.example.demo.dto.response.GoalResponse
import com.example.demo.repository.GoalRepository
import com.example.demo.repository.entity.GoalEntity
import com.example.demo.repository.entity.TaskEntity
import com.example.demo.repository.entity.UserProgressEntity
import com.example.demo.service.GoalService
import com.example.demo.utils.ProgressUpdater
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Optional
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GoalServiceTest {
    private val goalRepository = mockk<GoalRepository>(relaxed = true)
    private val progressUpdater = mockk<ProgressUpdater>(relaxed = true)
    private lateinit var service: GoalService

    private val userId = UUID.randomUUID()
    private val goalId = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        service = GoalService(goalRepository, progressUpdater)
    }

    @Test
    fun `save should persist goal and return response`() {
        val request = GoalRequest(userId = userId, text = "Test goal")
        val savedEntity =
            GoalEntity(
                id = UUID.randomUUID(),
                userId = userId,
                text = request.text,
                tasks = mutableListOf(),
            )
        val expectedResponse = savedEntity.toResponse()

        every { goalRepository.save(any<GoalEntity>()) } returns savedEntity

        val result = service.save(request)
        assertEquals(expectedResponse, result)
        verify { goalRepository.save(any<GoalEntity>()) }
    }

    @Test
    fun `findById should return response when goal exists`() {
        val entity = GoalEntity(id = goalId, userId = userId, text = "goal", tasks = mutableListOf())
        val expectedResponse = entity.toResponse()

        every { goalRepository.findById(goalId) } returns Optional.of(entity)

        val result = service.findById(goalId)
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `findById should throw when goal not found`() {
        every { goalRepository.findById(goalId) } returns Optional.empty()

        assertThrows<NoSuchElementException> { service.findById(goalId) }
    }

    @Test
    fun `findAllByUser should return list of responses`() {
        val entity1 = GoalEntity(id = UUID.randomUUID(), userId = userId, text = "g1", tasks = mutableListOf())
        val entity2 = GoalEntity(id = UUID.randomUUID(), userId = userId, text = "g2", tasks = mutableListOf())
        val response1 = entity1.toResponse()
        val response2 = entity2.toResponse()

        every { goalRepository.findAllByUserId(userId) } returns listOf(entity1, entity2)

        val result = service.findAllByUser(userId)
        assertEquals(listOf(response1, response2), result)
    }

    @Test
    fun `delete should call repository delete`() {
        every { goalRepository.deleteById(goalId) } just Runs

        service.delete(goalId)
        verify { goalRepository.deleteById(goalId) }
    }

    @Test
    fun `update should throw when goal not found`() {
        val request = GoalRequest(id = goalId, userId = userId, text = "any")
        every { goalRepository.findById(goalId) } returns Optional.empty()

        assertThrows<NoSuchElementException> { service.update(request) }
    }

    @Test
    fun `update should not call progressUpdater when completion state unchanged`() {
        val existingTask = TaskEntity(id = UUID.randomUUID(), userId = userId, text = "t1", completed = true, goal = null)
        val existing = GoalEntity(id = goalId, userId = userId, text = "old", tasks = mutableListOf(existingTask))
        val request =
            GoalRequest(
                id = goalId,
                userId = userId,
                text = "new",
                tasks = listOf(TaskRequest(id = existingTask.id, text = "t1", completed = true, userId = userId)),
            )

        every { goalRepository.findById(goalId) } returns Optional.of(existing)
        every { goalRepository.save(existing) } returns existing

        service.update(request)

        verify(exactly = 0) { progressUpdater.updateProgress(any(), any()) }
    }

    @Test
    fun `update should increment completedGoals when goal becomes fully completed`() {
        val task = TaskEntity(id = UUID.randomUUID(), userId = userId, text = "t1", completed = false, goal = null)
        val existing = GoalEntity(id = goalId, userId = userId, text = "old", tasks = mutableListOf(task))
        val request =
            GoalRequest(
                id = goalId,
                userId = userId,
                text = "new",
                tasks = listOf(TaskRequest(id = task.id, text = "t1", completed = true, userId = userId)),
            )

        every { goalRepository.findById(goalId) } returns Optional.of(existing)
        every { goalRepository.save(existing) } returns existing

        val updateSlot = slot<(UserProgressEntity) -> Unit>()
        every { progressUpdater.updateProgress(eq(userId), capture(updateSlot)) } answers { }

        service.update(request)

        assertTrue(updateSlot.isCaptured)
        val progress = UserProgressEntity(userId = userId, completedGoals = 0)
        updateSlot.captured.invoke(progress)
        assertEquals(1, progress.completedGoals)
    }

    @Test
    fun `update should decrement completedGoals when goal stops being fully completed`() {
        val task = TaskEntity(id = UUID.randomUUID(), userId = userId, text = "t1", completed = true, goal = null)
        val existing = GoalEntity(id = goalId, userId = userId, text = "old", tasks = mutableListOf(task))
        val request =
            GoalRequest(
                id = goalId,
                userId = userId,
                text = "new",
                tasks = listOf(TaskRequest(id = task.id, text = "t1", completed = false, userId = userId)),
            )

        every { goalRepository.findById(goalId) } returns Optional.of(existing)
        every { goalRepository.save(existing) } returns existing

        val updateSlot = slot<(UserProgressEntity) -> Unit>()
        every { progressUpdater.updateProgress(eq(userId), capture(updateSlot)) } just Runs

        service.update(request)

        assertTrue(updateSlot.isCaptured)
        val progress = UserProgressEntity(userId = userId, completedGoals = 5)
        updateSlot.captured.invoke(progress)
        assertEquals(4, progress.completedGoals)
    }
}
