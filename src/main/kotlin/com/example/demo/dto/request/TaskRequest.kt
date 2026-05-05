package com.example.demo.dto.request

import java.time.LocalDate
import java.util.UUID

data class TaskRequest(
    val id: UUID? = null,
    val userId: UUID,
    val text: String,
    val description: String? = null,
    val completed: Boolean = false,
    val date: LocalDate? = null,
)
