package com.example.demo.units

import com.example.demo.dto.mapper.toEntity
import com.example.demo.dto.mapper.toResponse
import com.example.demo.dto.request.TrackerProgressRequest
import com.example.demo.dto.request.TrackerRequest
import com.example.demo.repository.TrackerProgressRepository
import com.example.demo.repository.TrackerRepository
import com.example.demo.repository.entity.TrackerEntity
import com.example.demo.repository.entity.TrackerProgressEntity
import com.example.demo.service.TrackerService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID
import kotlin.test.assertTrue

class TrackerServiceTest {
    private lateinit var trackerRepository: TrackerRepository
    private lateinit var trackerProgressRepository: TrackerProgressRepository
    private lateinit var service: TrackerService

    private val userId = UUID.randomUUID()
    private val trackerId = UUID.randomUUID()
    private val progressId = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        trackerRepository = mockk()
        trackerProgressRepository = mockk()
        service = TrackerService(trackerRepository, trackerProgressRepository)
    }

    @Test
    fun `createTracker should save and return response`() {
        val request =
            TrackerRequest(
                name = "Water intake",
                unit = "ml",
                target = 2000,
                color = "#00FF00",
                icon = "",
                userId = UUID.randomUUID(),
            )
        val entity = request.toEntity(userId).copy(id = trackerId)
        val expectedResponse = entity.toResponse()

        every { trackerRepository.save(any<TrackerEntity>()) } returns entity

        val result = service.createTracker(userId, request)

        assertEquals(expectedResponse, result)
        verify(exactly = 1) { trackerRepository.save(any<TrackerEntity>()) }
    }

    @Test
    fun `getUserTrackers should return list of responses`() {
        val entity1 =
            TrackerEntity(id = UUID.randomUUID(), userId = userId, name = "Water", unit = "ml", target = 2000, color = "blue", icon = "")
        val entity2 =
            TrackerEntity(
                id = UUID.randomUUID(),
                userId = userId,
                name = "Steps",
                unit = "count",
                target = 10000,
                color = "green",
                icon = "",
            )
        val response1 = entity1.toResponse()
        val response2 = entity2.toResponse()

        every { trackerRepository.findAllByUserId(userId) } returns listOf(entity1, entity2)

        val result = service.getUserTrackers(userId)

        assertEquals(listOf(response1, response2), result)
        verify(exactly = 1) { trackerRepository.findAllByUserId(userId) }
    }

    @Test
    fun `deleteTracker should delete when tracker exists`() {
        val existing = TrackerEntity(id = trackerId, userId = userId, name = "Any", unit = "u", target = 1, color = "c", icon = "")

        every { trackerRepository.findByIdAndUserId(trackerId, userId) } returns existing
        every { trackerProgressRepository.deleteByTrackerId(trackerId) } returns Unit
        every { trackerRepository.delete(existing) } returns Unit

        assertDoesNotThrow { service.deleteTracker(userId, trackerId) }

        verify { trackerProgressRepository.deleteByTrackerId(trackerId) }
        verify { trackerRepository.delete(existing) }
    }

    @Test
    fun `deleteTracker should throw when tracker not found`() {
        every { trackerRepository.findByIdAndUserId(trackerId, userId) } returns null

        assertThrows<NoSuchElementException> {
            service.deleteTracker(userId, trackerId)
        }

        verify(exactly = 0) { trackerProgressRepository.deleteByTrackerId(any()) }
        verify(exactly = 0) { trackerRepository.delete(any()) }
    }

    @Test
    fun `saveProgress should update existing progress when found by trackerId and date`() {
        val date = "2025-01-01"
        val request = TrackerProgressRequest(trackerId = trackerId, date = date, value = 500, completed = true, userId = UUID.randomUUID())
        val existingEntity =
            TrackerProgressEntity(
                id = progressId,
                userId = userId,
                trackerId = trackerId,
                date = date,
                value = 100,
                completed = false,
            )
        val updatedEntity = existingEntity.copy(value = 500, completed = true)
        val expectedResponse = updatedEntity.toResponse()

        every { trackerProgressRepository.findByTrackerIdAndDate(trackerId, date) } returns existingEntity
        every { trackerProgressRepository.save(any<TrackerProgressEntity>()) } returns updatedEntity

        val result = service.saveProgress(userId, request)

        assertEquals(expectedResponse, result)
        verify(exactly = 1) { trackerProgressRepository.save(updatedEntity) }
    }

    @Test
    fun `saveProgress should create new progress when not found by trackerId and date`() {
        val date = "2025-01-01"
        val request = TrackerProgressRequest(trackerId = trackerId, date = date, value = 500, completed = true, userId = UUID.randomUUID())
        val newEntity = request.toEntity(userId).copy(id = progressId)
        val expectedResponse = newEntity.toResponse()

        every { trackerProgressRepository.findByTrackerIdAndDate(trackerId, date) } returns null
        every { trackerProgressRepository.save(any<TrackerProgressEntity>()) } returns newEntity

        val result = service.saveProgress(userId, request)

        assertEquals(expectedResponse, result)
        verify(exactly = 1) { trackerProgressRepository.save(any<TrackerProgressEntity>()) }
    }

    @Test
    fun `getProgress should return list of progress responses between dates`() {
        val startDate = "2025-01-01"
        val endDate = "2025-01-31"
        val entity1 =
            TrackerProgressEntity(
                id = UUID.randomUUID(),
                userId = userId,
                trackerId = trackerId,
                date = "2025-01-10",
                value = 100,
                completed = false,
            )
        val entity2 =
            TrackerProgressEntity(
                id = UUID.randomUUID(),
                userId = userId,
                trackerId = trackerId,
                date = "2025-01-20",
                value = 200,
                completed = true,
            )
        val response1 = entity1.toResponse()
        val response2 = entity2.toResponse()

        every { trackerProgressRepository.findByUserIdAndDateBetween(userId, startDate, endDate) } returns listOf(entity1, entity2)

        val result = service.getProgress(userId, startDate, endDate)

        assertEquals(listOf(response1, response2), result)
        verify(exactly = 1) { trackerProgressRepository.findByUserIdAndDateBetween(userId, startDate, endDate) }
    }

    @Test
    fun `getProgress should return empty list when no progress found`() {
        val startDate = "2025-01-01"
        val endDate = "2025-01-31"

        every { trackerProgressRepository.findByUserIdAndDateBetween(userId, startDate, endDate) } returns emptyList()

        val result = service.getProgress(userId, startDate, endDate)

        assertTrue(result.isEmpty())
        verify(exactly = 1) { trackerProgressRepository.findByUserIdAndDateBetween(userId, startDate, endDate) }
    }
}
