package com.example.demo.service

import com.example.demo.dto.mapper.toEntity
import com.example.demo.dto.mapper.toResponse
import com.example.demo.dto.request.TrackerProgressRequest
import com.example.demo.dto.request.TrackerRequest
import com.example.demo.dto.response.TrackerProgressResponse
import com.example.demo.dto.response.TrackerResponse
import com.example.demo.repository.TrackerProgressRepository
import com.example.demo.repository.TrackerRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class TrackerService(
    private val trackerRepository: TrackerRepository,
    private val trackerProgressRepository: TrackerProgressRepository,
) {
    @Transactional
    fun createTracker(
        userId: UUID,
        request: TrackerRequest,
    ): TrackerResponse =
        trackerRepository
            .save(request.toEntity(userId))
            .toResponse()

    @Transactional(readOnly = true)
    fun getUserTrackers(userId: UUID): List<TrackerResponse> = trackerRepository.findAllByUserId(userId).map { it.toResponse() }

    @Transactional
    fun deleteTracker(
        userId: UUID,
        trackerId: UUID,
    ) {
        val existing =
            trackerRepository.findByIdAndUserId(trackerId, userId)
                ?: throw NoSuchElementException("Tracker not found or access denied")
        trackerProgressRepository.deleteByTrackerId(trackerId)
        trackerRepository.delete(existing)
    }

    @Transactional
    fun saveProgress(
        userId: UUID,
        request: TrackerProgressRequest,
    ): TrackerProgressResponse {
        val existing = trackerProgressRepository.findByTrackerIdAndDate(request.trackerId, request.date)
        if (existing != null) {
            existing.value = request.value
            existing.completed = request.completed
            return trackerProgressRepository.save(existing).toResponse()
        } else {
            return trackerProgressRepository.save(request.toEntity(userId)).toResponse()
        }
    }

    @Transactional(readOnly = true)
    fun getProgress(
        userId: UUID,
        startDate: String,
        endDate: String,
    ): List<TrackerProgressResponse> =
        trackerProgressRepository
            .findByUserIdAndDateBetween(userId, startDate, endDate)
            .map { it.toResponse() }
}
