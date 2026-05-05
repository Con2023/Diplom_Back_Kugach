package com.example.demo.controller

import com.example.demo.dto.response.RecipeResponse
import com.example.demo.utils.RecipeData
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@Tag(name = "Рецепты")
@RequestMapping("/api/recipe")
class RecipeController {
    @GetMapping
    fun getRecipes(): List<RecipeResponse> = RecipeData.ALL

    @GetMapping("/{recipeId}")
    fun getRecipeById(
        @PathVariable recipeId: Int,
    ): RecipeResponse =
        RecipeData.ALL.firstOrNull { it.id == recipeId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe with id $recipeId not found")
}
