package com.example.demo.utils.data

import com.example.demo.model.AchievementDefinition
import com.example.demo.model.AchievementTier

object AchievementDefinitions {
    val ALL =
        listOf(
            AchievementDefinition(
                id = "complete_2_tasks",
                name = "Завершение",
                description = "Выпить 24 литра воды",
                icon = "💧",
                tier = AchievementTier.BRONZE,
                targetValue = 2,
                condition = { progress -> progress.completedTasks >= 2 },
            ),
            AchievementDefinition(
                id = "water_24_liters",
                name = "Гидратация",
                description = "Выпить 24 литра воды",
                icon = "💧",
                tier = AchievementTier.BRONZE,
                targetValue = 24,
                condition = { progress -> progress.waterLiters >= 24 },
            ),
            AchievementDefinition(
                id = "training_7_days",
                name = "Железная дисциплина",
                description = "Тренироваться 7 дней подряд",
                icon = "🔥",
                tier = AchievementTier.SILVER,
                targetValue = 7,
                condition = { progress -> progress.trainingStreak >= 7 },
            ),
            AchievementDefinition(
                id = "training_30_days",
                name = "Месяц без пропусков",
                description = "Тренироваться 30 дней подряд",
                icon = "📅",
                tier = AchievementTier.GOLD,
                targetValue = 30,
                condition = { progress -> progress.trainingStreak >= 30 },
            ),
            AchievementDefinition(
                id = "water_100_liters",
                name = "Океан внутри",
                description = "Выпить 100 литров воды",
                icon = "🌊",
                tier = AchievementTier.GOLD,
                targetValue = 100,
                condition = { progress -> progress.waterLiters >= 100 },
            ),
            AchievementDefinition(
                id = "goals_5_completed",
                name = "Целеустремлённый",
                description = "Завершить 5 целей",
                icon = "🎯",
                tier = AchievementTier.SILVER,
                targetValue = 5,
                condition = { progress -> progress.completedGoals >= 5 },
            ),
            AchievementDefinition(
                id = "goals_20_completed",
                name = "Мастер планирования",
                description = "Завершить 20 целей",
                icon = "🏆",
                tier = AchievementTier.GOLD,
                targetValue = 20,
                condition = { progress -> progress.completedGoals >= 20 },
            ),
            AchievementDefinition(
                id = "tasks_50_done",
                name = "Продуктивность",
                description = "Выполнить 50 задач",
                icon = "✅",
                tier = AchievementTier.BRONZE,
                targetValue = 50,
                condition = { progress -> progress.completedTasks >= 50 },
            ),
            AchievementDefinition(
                id = "early_bird_7",
                name = "Ранняя пташка",
                description = "Выполнить 7 тренировок до 8 утра",
                icon = "🌅",
                tier = AchievementTier.SILVER,
                targetValue = 7,
                condition = { progress -> progress.earlyWorkouts >= 7 },
            ),
            AchievementDefinition(
                id = "night_owl_7",
                name = "Ночной воин",
                description = "Выполнить 7 тренировок после 22:00",
                icon = "🦉",
                tier = AchievementTier.SILVER,
                targetValue = 7,
                condition = { progress -> progress.lateWorkouts >= 7 },
            ),
            AchievementDefinition(
                id = "strength_master",
                name = "Силовая легенда",
                description = "Выполнить 100 силовых тренировок",
                icon = "💪",
                tier = AchievementTier.GOLD,
                targetValue = 100,
                condition = { progress -> progress.strengthWorkouts >= 100 },
            ),
        )
}
