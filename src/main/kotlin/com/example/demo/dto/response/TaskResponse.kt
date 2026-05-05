package com.example.demo.dto.response

import java.time.LocalDate
import java.util.UUID

data class TaskResponse(
    val id: UUID? = null,
    val userId: UUID,
    val text: String,
    val description: String? = null,
    val completed: Boolean,
    val date: LocalDate? = null,
)
