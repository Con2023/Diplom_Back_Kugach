package com.example.demo.utils

import com.example.demo.repository.UserProgressRepository
import com.example.demo.repository.entity.UserProgressEntity
import com.example.demo.service.AchievementService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Component
class ProgressUpdater(
    private val userProgressRepo: UserProgressRepository,
    private val achievementService: AchievementService,
) {
    @Transactional
    fun updateProgress(
        userId: UUID,
        action: (UserProgressEntity) -> Unit,
    ) {
        val progress =
            userProgressRepo
                .findById(userId)
                .orElseGet { UserProgressEntity(userId = userId) }
        action(progress)
        userProgressRepo.save(progress)
        achievementService.checkAndUnlock(userId)
    }
}
