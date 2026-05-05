package com.example.demo.repository

import com.example.demo.repository.entity.JournalEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface JournalRepository : JpaRepository<JournalEntity, UUID> {
    fun findAllByUserIdOrderByDateDesc(userId: UUID): List<JournalEntity>

    fun findByIdAndUserId(
        id: UUID,
        userId: UUID,
    ): JournalEntity?

    fun findAllByUserId(userId: UUID): List<JournalEntity>
}
