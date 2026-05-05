package com.example.demo.dto.response

data class RecipeResponse(
    val id: Int? = null,
    val title: String,
    val emoji: String,
    val description: String,
    val calories: Int,
    val cookTime: Int,
    val servings: Int,
    val difficulty: String,
    val category: String,
    val tags: List<String>,
    val protein: Int,
    val fats: Int,
    val carbs: Int,
    val ingredients: List<String>,
    val instructions: List<String>,
    val liked: Boolean = false,
)
