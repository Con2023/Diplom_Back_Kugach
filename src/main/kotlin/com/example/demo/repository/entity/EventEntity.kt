package com.example.demo.repository.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "event")
data class EventEntity(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    @Column(name = "user_id", nullable = false, columnDefinition = "UUID")
    val userId: UUID,
    @Column(nullable = false)
    var text: String,
    @Column(name = "start_time")
    var startTime: String,
    @Column(name = "end_time")
    var endTime: String? = null,
    var date: String,
    var completed: Boolean = false,
)
