package com.example.demo.dto.response

import java.util.UUID

data class FoodEntryResponse(
    val id: UUID? = null,
    val userId: String? = null,
    val name: String,
    val calories: Int,
    val protein: Int,
    val fats: Int,
    val carbs: Int,
    val mealType: String,
    val time: String,
    val date: String,
)
