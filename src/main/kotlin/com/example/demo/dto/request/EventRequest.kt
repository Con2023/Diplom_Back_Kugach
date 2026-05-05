package com.example.demo.dto.request

import java.util.UUID

data class EventRequest(
    val id: UUID? = null,
    val userId: UUID,
    val text: String,
    val startTime: String,
    val endTime: String? = null,
    val date: String,
    val completed: Boolean = false,
)
