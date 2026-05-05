package com.example.demo.integrations

import com.example.demo.configs.AuthenticatedUser
import com.example.demo.controller.AchievementController
import com.example.demo.dto.response.AchievementResponse
import com.example.demo.service.AchievementService
import com.example.demo.utils.Role
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import java.util.UUID

@WebMvcTest(AchievementController::class)
class AchievementControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var achievementService: AchievementService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
    private val authenticatedUser =
        AuthenticatedUser(
            id = userId,
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
    fun `getMyAchievements should return list of achievements for authenticated user`() {
        authenticateUser()
        val achievements =
            listOf(
                AchievementResponse(
                    id = "first_steps",
                    name = "First Steps",
                    description = "Completed first goal",
                    unlocked = true,
                    unlockedAt = LocalDateTime.now(),
                    progress = 10,
                    tier = "",
                    icon = "",
                ),
                AchievementResponse(
                    id = "marathon",
                    name = "Marathon",
                    description = "1000 steps",
                    unlocked = false,
                    unlockedAt = null,
                    progress = 100,
                    tier = "",
                    icon = "",
                ),
            )

        `when`(achievementService.getUserAchievements(userId)).thenReturn(achievements)

        mockMvc
            .perform(get("/api/achievements/me"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value("first_steps"))
            .andExpect(jsonPath("$[0].unlocked").value(true))
            .andExpect(jsonPath("$[1].id").value("marathon"))
            .andExpect(jsonPath("$[1].unlocked").value(false))
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    fun `getMyAchievements should return empty list when user has no achievements`() {
        authenticateUser()
        `when`(achievementService.getUserAchievements(userId)).thenReturn(emptyList())

        mockMvc
            .perform(get("/api/achievements/me"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(0))
    }

    @Test
    fun `getMyAchievements should return unauthorized when not authenticated`() {
        mockMvc
            .perform(get("/api/achievements/me"))
            .andExpect(status().isUnauthorized)
    }
}
