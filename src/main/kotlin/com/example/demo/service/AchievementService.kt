package com.example.demo.service

import com.example.demo.dto.response.AchievementResponse
import com.example.demo.repository.UserAchievementRepository
import com.example.demo.repository.UserProgressRepository
import com.example.demo.repository.entity.UserAchievementEntity
import com.example.demo.repository.entity.UserProgressEntity
import com.example.demo.utils.data.AchievementDefinitions
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class AchievementService(
    private val userProgressRepo: UserProgressRepository,
    private val userAchievementRepo: UserAchievementRepository,
) {
    @Transactional
    fun checkAndUnlock(userId: UUID) {
        val progress =
            userProgressRepo.findByUserId(userId)
                ?: UserProgressEntity(userId = userId)

        AchievementDefinitions.ALL.forEach { def ->
            val ua =
                userAchievementRepo.findByUserIdAndAchievementId(userId, def.id)
                    ?: UserAchievementEntity(userId = userId, achievementId = def.id)

            if (!ua.unlocked && def.condition(progress)) {
                ua.unlocked = true
                ua.unlockedAt = LocalDateTime.now()
                userAchievementRepo.save(ua)
            }
        }

        userProgressRepo.save(progress)
    }

    @Transactional
    fun getUserAchievements(userId: UUID): List<AchievementResponse> {
        val progress = userProgressRepo.findById(userId).orElse(null)
        val userAchievements =
            userAchievementRepo
                .findAllByUserId(userId)
                .associateBy { it.achievementId }

        return AchievementDefinitions.ALL.map { def ->
            val ua = userAchievements[def.id]
            val unlocked = ua?.unlocked ?: false
            val progressValue =
                when (def.id) {
                    "water_24_liters", "water_100_liters" -> (progress?.waterLiters?.toInt() ?: 0)
                    "training_7_days", "training_30_days" -> (progress?.trainingStreak ?: 0)
                    "goals_5_completed", "goals_20_completed" -> (progress?.completedGoals ?: 0)
                    "tasks_50_done" -> (progress?.completedTasks ?: 0)
                    "early_bird_7" -> (progress?.earlyWorkouts ?: 0)
                    "night_owl_7" -> (progress?.lateWorkouts ?: 0)
                    "strength_master" -> (progress?.strengthWorkouts ?: 0)
                    else -> 0
                }
            AchievementResponse(
                id = def.id,
                name = def.name,
                description = def.description,
                icon = def.icon,
                tier = def.tier.name,
                unlocked = unlocked,
                unlockedAt = ua?.unlockedAt,
                progress = progressValue,
                maxProgress = def.targetValue,
            )
        }
    }
}
