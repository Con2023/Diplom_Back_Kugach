package com.example.demo.dto.request

import java.util.UUID

data class TrackerProgressRequest(
    val id: UUID? = null,
    val userId: UUID,
    val trackerId: UUID,
    val date: String,
    val value: Int,
    val completed: Boolean,
)
