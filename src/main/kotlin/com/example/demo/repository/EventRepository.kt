package com.example.demo.repository

import com.example.demo.repository.entity.EventEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface EventRepository : JpaRepository<EventEntity, UUID> {
    fun findAllByUserId(userId: UUID): List<EventEntity>
}
