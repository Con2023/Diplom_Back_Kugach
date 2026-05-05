package com.example.demo.dto.response

import java.util.UUID

data class TrackerResponse(
    val id: UUID? = null,
    val userId: String,
    val name: String,
    val icon: String,
    val target: Int,
    val unit: String,
    val color: String,
    val category: String,
    val weeklyTarget: Int? = null,
)
