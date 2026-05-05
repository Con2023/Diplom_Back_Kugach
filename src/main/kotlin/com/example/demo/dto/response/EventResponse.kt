package com.example.demo.dto.response

import java.util.UUID

data class EventResponse(
    val id: UUID? = null,
    val userId: UUID? = null,
    val text: String,
    val startTime: String,
    val endTime: String? = null,
    val date: String,
    val completed: Boolean,
)
