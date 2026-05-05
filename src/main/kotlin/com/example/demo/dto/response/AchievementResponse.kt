package com.example.demo.dto.response

import java.time.LocalDateTime

data class AchievementResponse(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val tier: String,
    val unlocked: Boolean,
    val unlockedAt: LocalDateTime?,
    val progress: Int,
    val maxProgress: Int = 0,
)
