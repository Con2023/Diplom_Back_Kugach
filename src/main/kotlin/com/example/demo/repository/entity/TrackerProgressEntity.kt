package com.example.demo.repository.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "tracker_progress")
data class TrackerProgressEntity(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    @Column(name = "tracker_id", nullable = false)
    val trackerId: UUID,
    @Column(name = "user_id", nullable = false)
    val userId: UUID,
    @Column(nullable = false)
    var date: String,
    @Column(nullable = false)
    var value: Int,
    @Column(nullable = false)
    var completed: Boolean,
)
