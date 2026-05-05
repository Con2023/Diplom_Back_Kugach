package com.example.demo.model

import com.example.demo.repository.entity.UserProgressEntity

data class AchievementDefinition(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val tier: AchievementTier,
    val targetValue: Int,
    val condition: (UserProgressEntity) -> Boolean,
)
