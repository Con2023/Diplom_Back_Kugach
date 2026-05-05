package com.example.demo.dto.request

import java.util.UUID

data class TrackerRequest(
    val id: UUID? = null,
    val userId: UUID,
    val name: String,
    val icon: String,
    val target: Int,
    val unit: String,
    val color: String,
    val category: String = "custom",
    val weeklyTarget: Int? = null,
)
