package com.example.demo.repository

import com.example.demo.repository.entity.TrackerProgressEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TrackerProgressRepository : JpaRepository<TrackerProgressEntity, UUID> {
    fun findByTrackerIdAndDate(
        trackerId: UUID,
        date: String,
    ): TrackerProgressEntity?

    fun findByUserIdAndDateBetween(
        userId: UUID,
        startDate: String,
        endDate: String,
    ): List<TrackerProgressEntity>

    fun deleteByTrackerId(trackerId: UUID)
}
