package com.example.demo.dto.mapper

import com.example.demo.dto.request.FoodEntryRequest
import com.example.demo.dto.response.FoodEntryResponse
import com.example.demo.repository.entity.FoodEntity
import java.util.UUID

fun FoodEntryRequest.toEntity(userId: UUID): FoodEntity =
    FoodEntity(
        userId = userId,
        name = name,
        calories = calories,
        protein = protein,
        fats = fats,
        carbs = carbs,
        mealType = mealType,
        time = time,
        date = date,
    )

fun FoodEntity.toResponse(): FoodEntryResponse =
    FoodEntryResponse(
        id = id,
        userId = userId.toString(),
        name = name,
        calories = calories,
        protein = protein,
        fats = fats,
        carbs = carbs,
        mealType = mealType,
        time = time,
        date = date,
    )
