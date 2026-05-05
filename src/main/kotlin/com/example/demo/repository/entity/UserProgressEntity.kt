package com.example.demo.repository.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "user_progress")
data class UserProgressEntity(
    @Id
    val userId: UUID,
    var waterLiters: Float = 0f,
    var trainingStreak: Int = 0,
    var lastTrainingDate: LocalDate? = null,
    var completedGoals: Int = 0,
    var completedTasks: Int = 0,
    var earlyWorkouts: Int = 0,
    var lateWorkouts: Int = 0,
    var strengthWorkouts: Int = 0,
)
