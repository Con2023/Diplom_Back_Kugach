package com.example.demo.dto.request

import java.util.UUID

data class FoodEntryRequest(
    val id: UUID? = null,
    val userId: UUID,
    val name: String,
    val calories: Int,
    val protein: Int,
    val fats: Int,
    val carbs: Int,
    val mealType: String,
    val time: String,
    val date: String,
)
