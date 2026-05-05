package com.example.demo.dto.response

import java.util.UUID

data class TrackerProgressResponse(
    val id: UUID? = null,
    val trackerId: UUID,
    val userId: String,
    val date: String,
    val value: Int,
    val completed: Boolean,
)
