package com.example.demo.repository

import com.example.demo.repository.entity.GoalEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface GoalRepository : JpaRepository<GoalEntity, UUID> {
    fun findAllByUserId(userId: UUID): List<GoalEntity>
}
