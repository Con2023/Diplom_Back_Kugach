package com.example.demo.repository

import com.example.demo.repository.entity.TrackerEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TrackerRepository : JpaRepository<TrackerEntity, UUID> {
    fun findAllByUserId(userId: UUID): List<TrackerEntity>

    fun findByIdAndUserId(
        id: UUID,
        userId: UUID,
    ): TrackerEntity?
}
