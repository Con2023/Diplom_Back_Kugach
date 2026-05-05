package com.example.demo.integrations

import com.example.demo.configs.AuthenticatedUser
import com.example.demo.controller.FoodController
import com.example.demo.dto.request.FoodEntryRequest
import com.example.demo.dto.response.FoodEntryResponse
import com.example.demo.service.FoodService
import com.example.demo.utils.Role
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@WebMvcTest(FoodController::class)
class FoodControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var foodService: FoodService

    private val authenticatedUser =
        AuthenticatedUser(
            id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
            email = "test@example.com",
            roles = setOf(Role.ROLE_USER),
        )

    @AfterEach
    fun clearSecurityContext() {
        SecurityContextHolder.clearContext()
    }

    private fun authenticateUser() {
        val authentication =
            object : Authentication {
                override fun getName(): String = authenticatedUser.email

                override fun getAuthorities(): MutableCollection<out GrantedAuthority> = mutableListOf(SimpleGrantedAuthority("ROLE_USER"))

                override fun getCredentials(): Any = ""

                override fun getDetails(): Any? = null

                override fun getPrincipal(): Any = authenticatedUser

                override fun isAuthenticated(): Boolean = true

                override fun setAuthenticated(isAuthenticated: Boolean) {}
            }
        SecurityContextHolder.getContext().authentication = authentication
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    fun `getUserEntries should return list for owner`() {
        authenticateUser()
        val userId = authenticatedUser.id
        val response =
            FoodEntryResponse(
                id = UUID.randomUUID(),
                userId = userId.toString(),
                name = "Apple",
                calories = 95,
                protein = 0,
                fats = 0,
                carbs = 25,
                mealType = "SNACK",
                time = "10:00",
                date = "2026-05-05",
            )
        `when`(foodService.getUserEntries(userId)).thenReturn(listOf(response))

        mockMvc
            .perform(
                get("/api/food/user/{userId}", userId)
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$[0].name").value("Apple"))
            .andExpect(jsonPath("$[0].calories").value(95))
    }

    @Test
    fun `getUserEntries should return unauthorized when not authenticated`() {
        val someUserId = UUID.randomUUID()
        mockMvc
            .perform(
                get("/api/food/user/{userId}", someUserId)
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    fun `getUserEntries should return forbidden when user id does not match`() {
        authenticateUser()
        val otherUserId = UUID.randomUUID()
        mockMvc
            .perform(
                get("/api/food/user/{userId}", otherUserId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    fun `deleteEntry should return no content`() {
        authenticateUser()
        val entryId = UUID.randomUUID()
        `when`(foodService.deleteEntry(authenticatedUser.id, entryId)).thenAnswer { } // void

        mockMvc
            .perform(
                delete("/api/food/{entryId}", entryId)
                    .with(csrf()),
            ).andExpect(status().isNoContent)
    }
}
