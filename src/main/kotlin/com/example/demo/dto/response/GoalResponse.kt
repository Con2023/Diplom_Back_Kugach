package com.example.demo.dto.response

import java.util.UUID

data class GoalResponse(
    val id: UUID? = null,
    val userId: UUID,
    val text: String,
    val category: String? = null,
    val priority: String? = null,
    val deadline: String? = null,
    val tasks: List<TaskResponse>,
)
