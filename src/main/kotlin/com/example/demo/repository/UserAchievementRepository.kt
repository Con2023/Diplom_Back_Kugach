package com.example.demo.repository

import com.example.demo.repository.entity.UserAchievementEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserAchievementRepository : JpaRepository<UserAchievementEntity, UUID> {
    fun findByUserIdAndAchievementId(
        userId: UUID,
        achievementId: String,
    ): UserAchievementEntity?

    fun findAllByUserId(userId: UUID): List<UserAchievementEntity>
}
