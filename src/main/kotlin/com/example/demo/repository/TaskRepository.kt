package com.example.demo.repository

import com.example.demo.repository.entity.TaskEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate
import java.util.UUID

interface TaskRepository : JpaRepository<TaskEntity, UUID> {
    fun findAllByUserId(userId: UUID): List<TaskEntity>

    fun findByUserIdAndDate(
        userId: UUID,
        date: LocalDate,
    ): List<TaskEntity>
}
