package com.example.demo.units

import com.example.demo.dto.request.EventRequest
import com.example.demo.repository.EventRepository
import com.example.demo.repository.entity.EventEntity
import com.example.demo.service.EventService
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Optional
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EventServiceTest {
    private val repo = mockk<EventRepository>(relaxed = true)
    private val service = EventService(repo)

    @Test
    fun `update should update event fields and return response`() {
        val id = UUID.randomUUID()
        val request =
            EventRequest(
                id = id,
                userId = id,
                text = "new text",
                startTime = "09:00",
                endTime = "10:00",
                date = "2026-05-05",
                completed = true,
            )
        val existing =
            EventEntity(
                id = id,
                userId = UUID.randomUUID(),
                text = "old",
                startTime = "08:00",
                endTime = null,
                date = "2026-05-04",
                completed = false,
            )
        every { repo.findById(id) } returns Optional.of(existing)
        every { repo.save(any()) } returns existing

        val response = service.update(request)
        assertEquals("new text", response.text)
        assertEquals("09:00", response.startTime)
        verify { repo.save(existing) }
    }

    @Test
    fun `save should persist event and return response`() {
        val request = EventRequest(userId = UUID.randomUUID(), text = "event", startTime = "09:00", date = "2026-05-05")
        val entity =
            EventEntity(id = UUID.randomUUID(), userId = UUID.randomUUID(), text = "event", startTime = "09:00", date = "2026-05-05")
        every { repo.save(any()) } returns entity

        val response = service.save(request)
        assertEquals("event", response.text)
        verify { repo.save(any()) }
    }

    @Test
    fun `findAllByUser should return list of events`() {
        val userId = UUID.randomUUID()
        every { repo.findAllByUserId(userId) } returns emptyList()
        val result = service.findAllByUser(userId)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `update should modify fields and save`() {
        val id = UUID.randomUUID()
        val existing = EventEntity(id = id, userId = UUID.randomUUID(), text = "old", startTime = "08:00", date = "2026-05-04")
        every { repo.findById(id) } returns Optional.of(existing)
        every { repo.save(existing) } returns existing

        val request =
            EventRequest(
                id = id,
                text = "new",
                startTime = "09:00",
                endTime = "10:00",
                date = "2026-05-05",
                completed = true,
                userId = UUID.randomUUID(),
            )
        val response = service.update(request)

        assertEquals("new", response.text)
        assertEquals("09:00", response.startTime)
        assertTrue(response.completed)
        verify { repo.save(existing) }
    }

    @Test
    fun `delete should throw if event not found`() {
        val id = UUID.randomUUID()
        every { repo.existsById(id) } returns false
        assertThrows<NoSuchElementException> { service.delete(id) }
    }

    @Test
    fun `delete should succeed if event exists`() {
        val id = UUID.randomUUID()
        every { repo.existsById(id) } returns true
        every { repo.deleteById(id) } just Runs
        assertDoesNotThrow { service.delete(id) }
        verify { repo.deleteById(id) }
    }
}
