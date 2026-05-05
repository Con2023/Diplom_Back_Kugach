package com.example.demo.service

import com.example.demo.dto.mapper.toEntity
import com.example.demo.dto.mapper.toResponse
import com.example.demo.dto.request.EventRequest
import com.example.demo.dto.response.EventResponse
import com.example.demo.repository.EventRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class EventService(
    private val eventRepository: EventRepository,
) {
    @Transactional
    fun save(request: EventRequest): EventResponse =
        eventRepository
            .save(request.toEntity())
            .toResponse()

    @Transactional
    fun findAllByUser(userId: UUID): List<EventResponse> = eventRepository.findAllByUserId(userId).map { it.toResponse() }

    @Transactional
    fun update(request: EventRequest): EventResponse {
        val id = request.id ?: throw IllegalArgumentException("Event id required for update")
        val existing =
            eventRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("Event not found: $id") }

        existing.text = request.text
        existing.startTime = request.startTime
        existing.endTime = request.endTime
        existing.date = request.date
        existing.completed = request.completed

        return eventRepository
            .save(existing)
            .toResponse()
    }

    @Transactional
    fun delete(eventId: UUID) {
        if (!eventRepository.existsById(eventId)) {
            throw NoSuchElementException("Event not found: $eventId")
        }
        eventRepository.deleteById(eventId)
    }
}
