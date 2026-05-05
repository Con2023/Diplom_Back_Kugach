package com.example.demo.integrations

import com.example.demo.configs.AuthenticatedUser
import com.example.demo.controller.StatisticsController
import com.example.demo.dto.response.StatisticsResponse
import com.example.demo.service.StatisticsService
import com.example.demo.utils.Role
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@WebMvcTest(StatisticsController::class)
class StatisticsControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var statisticsService: StatisticsService

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
    fun `getMyStatistics should return statistics when authenticated`() {
        authenticateUser()

        val statisticsResponse =
            StatisticsResponse(
                emotionData = emptyList(),
                moodScore = 20,
            )

        org.mockito.kotlin
            .whenever(statisticsService.getStatistics(userId))
            .thenReturn(statisticsResponse)

        mockMvc
            .perform(get("/api/statistics/me"))
            .andExpect(status().isOk)
    }

    @Test
    fun `getMyStatistics should return unauthorized when not authenticated`() {
        mockMvc
            .perform(get("/api/statistics/me"))
            .andExpect(status().isUnauthorized)
    }
}
