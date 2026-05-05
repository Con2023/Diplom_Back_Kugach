package com.example.demo.service

import com.example.demo.dto.request.JournalEntryRequest
import com.example.demo.dto.response.JournalEntryResponse
import com.example.demo.repository.JournalRepository
import com.example.demo.repository.entity.JournalEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class JournalService(
    private val journalRepository: JournalRepository,
) {
    @Transactional
    fun createEntry(
        userId: UUID,
        request: JournalEntryRequest,
    ): JournalEntryResponse {
        val entity =
            JournalEntity(
                userId = userId,
                type = request.type,
                date = request.date,
                title = request.title,
                content = request.content,
                mood = request.mood,
                tags = request.tags,
            )
        val saved = journalRepository.save(entity)
        return toResponse(saved)
    }

    @Transactional(readOnly = true)
    fun getUserEntries(userId: UUID): List<JournalEntryResponse> =
        journalRepository
            .findAllByUserIdOrderByDateDesc(userId)
            .map { toResponse(it) }

    @Transactional
    fun updateEntry(
        userId: UUID,
        entryId: UUID,
        request: JournalEntryRequest,
    ): JournalEntryResponse {
        val existing =
            journalRepository.findByIdAndUserId(entryId, userId)
                ?: throw NoSuchElementException("Journal entry not found")
        existing.type = request.type
        existing.date = request.date
        existing.title = request.title
        existing.content = request.content
        existing.mood = request.mood
        existing.tags = request.tags
        val updated = journalRepository.save(existing)
        return toResponse(updated)
    }

    @Transactional
    fun deleteEntry(
        userId: UUID,
        entryId: UUID,
    ) {
        val entry =
            journalRepository.findByIdAndUserId(entryId, userId)
                ?: throw NoSuchElementException("Journal entry not found")
        journalRepository.delete(entry)
    }

    private fun toResponse(entity: JournalEntity): JournalEntryResponse =
        JournalEntryResponse(
            id = entity.id,
            userId = entity.userId.toString(),
            type = entity.type,
            date = entity.date,
            title = entity.title,
            content = entity.content,
            mood = entity.mood,
            tags = entity.tags,
        )
}
