package com.example.demo.integrations

import com.example.demo.controller.RecipeController
import com.example.demo.utils.RecipeData
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(RecipeController::class)
class RecipeControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @WithMockUser
    fun `getRecipes should return all recipes with 200`() {
        val firstRecipe = RecipeData.ALL.first()

        mockMvc
            .perform(
                get("/api/recipe")
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(firstRecipe.id))
            .andExpect(jsonPath("$[0].title").value(firstRecipe.title))
    }

    @Test
    @WithMockUser
    fun `getRecipeById should return recipe if exists`() {
        val recipe = RecipeData.ALL.first()

        mockMvc
            .perform(
                get("/api/recipe/{recipeId}", recipe.id)
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(recipe.id))
            .andExpect(jsonPath("$.title").value(recipe.title))
    }

    @Test
    @WithMockUser
    fun `getRecipeById should return 404 if not found`() {
        val nonExistentId = 9999

        mockMvc
            .perform(
                get("/api/recipe/{recipeId}", nonExistentId)
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isNotFound)
    }
}
