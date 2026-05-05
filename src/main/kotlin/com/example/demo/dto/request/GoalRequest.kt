package com.example.demo.dto.request

import java.util.UUID

data class GoalRequest(
    val id: UUID? = null,
    val userId: UUID,
    val text: String,
    val category: String? = null,
    val priority: String? = null,
    val deadline: String? = null,
    val tasks: List<TaskRequest> = emptyList(),
)
