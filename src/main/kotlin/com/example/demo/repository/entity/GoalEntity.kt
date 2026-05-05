package com.example.demo.repository.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "goal")
data class GoalEntity(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    @Column(name = "user_id", nullable = false)
    val userId: UUID,
    @Column(nullable = false)
    var text: String,
    var category: String? = null,
    var priority: String? = null,
    var deadline: String? = null,
    @OneToMany(mappedBy = "goal", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var tasks: MutableList<TaskEntity> = mutableListOf(),
    @Column(name = "created_at")
    val createdAt: LocalDate = LocalDate.now(),
)
