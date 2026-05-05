package com.example.demo.repository

import com.example.demo.repository.entity.UserProgressEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserProgressRepository : JpaRepository<UserProgressEntity, UUID> {
    fun findByUserId(userId: UUID): UserProgressEntity?
}
