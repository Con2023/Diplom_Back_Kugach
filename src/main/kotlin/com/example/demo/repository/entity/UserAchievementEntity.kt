package com.example.demo.repository.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "user_achievement")
data class UserAchievementEntity(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    val userId: UUID,
    val achievementId: String,
    var unlocked: Boolean = false,
    var unlockedAt: LocalDateTime? = null,
    var progress: Int = 0,
)
