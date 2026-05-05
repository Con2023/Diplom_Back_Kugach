package com.example.demo.units

import com.example.demo.dto.request.JournalEntryRequest
import com.example.demo.repository.JournalRepository
import com.example.demo.repository.entity.JournalEntity
import com.example.demo.service.JournalService
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID
import kotlin.test.assertEquals

class JournalServiceTest {
    private val journalRepository = mockk<JournalRepository>(relaxed = true)
    private val service = JournalService(journalRepository)

    private val userId = UUID.randomUUID()
    private val entryId = UUID.randomUUID()

    @Test
    fun `createEntry should save and return response`() {
        val request =
            JournalEntryRequest(
                type = "EMOTIONS",
                date = "2026-05-05",
                title = "Test title",
                content = "Test content",
                mood = "HAPPY",
                tags = listOf("tag1", "tag2"),
            )
        val savedEntity =
            JournalEntity(
                id = entryId,
                userId = userId,
                type = request.type,
                date = request.date,
                title = request.title,
                content = request.content,
                mood = request.mood,
                tags = request.tags,
            )
        every { journalRepository.save(any<JournalEntity>()) } returns savedEntity

        val result = service.createEntry(userId, request)
        assertEquals(entryId, result.id)
        assertEquals(userId.toString(), result.userId)
        assertEquals(request.type, result.type)
        assertEquals(request.title, result.title)
        assertEquals(request.content, result.content)
        assertEquals(request.mood, result.mood)
        assertEquals(request.tags, result.tags)
        verify { journalRepository.save(match { it.userId == userId && it.title == request.title }) }
    }

    @Test
    fun `getUserEntries should return list of responses`() {
        val entity1 =
            JournalEntity(
                id = UUID.randomUUID(),
                userId = userId,
                type = "THOUGHTS",
                date = "2026-05-05",
                title = "Title1",
                content = "C1",
                mood = "NEUTRAL",
                tags = emptyList(),
            )
        val entity2 =
            JournalEntity(
                id = UUID.randomUUID(),
                userId = userId,
                type = "DREAMS",
                date = "2026-05-04",
                title = "Title2",
                content = "C2",
                mood = "HAPPY",
                tags = listOf("x"),
            )
        every { journalRepository.findAllByUserIdOrderByDateDesc(userId) } returns listOf(entity1, entity2)

        val result = service.getUserEntries(userId)
        assertEquals(2, result.size)
        assertEquals("Title1", result[0].title)
        assertEquals("Title2", result[1].title)
    }

    @Test
    fun `updateEntry should throw when entry not found`() {
        val request =
            JournalEntryRequest(
                type = "EMOTIONS",
                date = "2026-05-05",
                title = "Updated",
                content = "Updated content",
                mood = "SAD",
                tags = emptyList(),
            )
        every { journalRepository.findByIdAndUserId(entryId, userId) } returns null

        assertThrows<NoSuchElementException> {
            service.updateEntry(userId, entryId, request)
        }
    }

    @Test
    fun `updateEntry should update fields and return response`() {
        val existing =
            JournalEntity(
                id = entryId,
                userId = userId,
                type = "OLD",
                date = "2026-01-01",
                title = "Old title",
                content = "Old content",
                mood = "NEUTRAL",
                tags = listOf("old"),
            )
        val request =
            JournalEntryRequest(
                type = "EMOTIONS",
                date = "2026-05-05",
                title = "New title",
                content = "New content",
                mood = "HAPPY",
                tags = listOf("new"),
            )
        every { journalRepository.findByIdAndUserId(entryId, userId) } returns existing
        every { journalRepository.save(existing) } returns existing

        val result = service.updateEntry(userId, entryId, request)
        assertEquals("New title", result.title)
        assertEquals("HAPPY", result.mood)
        assertEquals(listOf("new"), result.tags)
        verify { journalRepository.save(existing) }
    }

    @Test
    fun `deleteEntry should throw when entry not found`() {
        every { journalRepository.findByIdAndUserId(entryId, userId) } returns null

        assertThrows<NoSuchElementException> {
            service.deleteEntry(userId, entryId)
        }
    }

    @Test
    fun `deleteEntry should delete entry when found`() {
        val existing = mockk<JournalEntity>(relaxed = true)
        every { journalRepository.findByIdAndUserId(entryId, userId) } returns existing
        every { journalRepository.delete(existing) } just Runs

        service.deleteEntry(userId, entryId)
        verify { journalRepository.delete(existing) }
    }
}
