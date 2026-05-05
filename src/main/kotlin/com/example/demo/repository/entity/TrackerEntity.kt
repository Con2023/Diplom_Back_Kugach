package com.example.demo.repository.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "tracker")
data class TrackerEntity(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    @Column(name = "user_id", nullable = false)
    val userId: UUID,
    @Column(nullable = false)
    var name: String,
    @Column(nullable = false)
    var icon: String,
    @Column(nullable = false)
    var target: Int,
    @Column(nullable = false)
    var unit: String,
    @Column(nullable = false)
    var color: String,
    @Column(nullable = false)
    var category: String = "custom",
    @Column(name = "weekly_target")
    var weeklyTarget: Int? = null,
)
