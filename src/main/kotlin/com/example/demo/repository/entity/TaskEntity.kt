package com.example.demo.repository.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "task")
data class TaskEntity(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    @Column(name = "user_id", nullable = false, columnDefinition = "UUID")
    val userId: UUID,
    @Column(nullable = false)
    var text: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id")
    var goal: GoalEntity? = null,
    var description: String? = null,
    var completed: Boolean = false,
    var date: LocalDate? = LocalDate.now(),
)
