package com.example.demo.integrations

import com.example.demo.configs.AuthenticatedUser
import com.example.demo.controller.TrackerController
import com.example.demo.dto.request.TrackerProgressRequest
import com.example.demo.dto.request.TrackerRequest
import com.example.demo.dto.response.TrackerProgressResponse
import com.example.demo.dto.response.TrackerResponse
import com.example.demo.service.TrackerService
import com.example.demo.utils.Role
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.any
import org.mockito.Mockito.`when`
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@WebMvcTest(TrackerController::class)
class TrackerControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var trackerService: TrackerService

    private val userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
    private val authenticatedUser =
        AuthenticatedUser(
            id = userId,
            email = "test@example.com",
            roles = setOf(Role.ROLE_USER),
        )

    @AfterEach
    fun clearSecurityContext() {
        SecurityContextHolder.clearContext() // очистка после каждого теста
    }

    private fun authenticateUser() {
        val authentication =
            object : Authentication {
                override fun getName(): String = authenticatedUser.email

                override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
                    mutableListOf(
                        SimpleGrantedAuthority("ROLE_USER"),
                    )

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
    fun `getUserTrackers should return list when user is authenticated and authorized`() {
        authenticateUser()
        val trackerResponse =
            TrackerResponse(
                id = UUID.randomUUID(),
                userId = userId.toString(),
                name = "Test Tracker",
                icon = "💧",
                target = 5,
                unit = "стаканов",
                color = "PRIMARY",
                category = "custom",
                weeklyTarget = null,
            )
        `when`(trackerService.getUserTrackers(userId)).thenReturn(listOf(trackerResponse))

        mockMvc
            .perform(
                get("/api/tracker/user/{userId}", userId)
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(trackerResponse.id.toString()))
            .andExpect(jsonPath("$[0].name").value("Test Tracker"))
    }

    @Test
    fun `getUserTrackers should return forbidden when user id does not match authenticated user`() {
        val otherUserId = UUID.randomUUID()
        `when`(trackerService.getUserTrackers(otherUserId)).thenReturn(emptyList())

        mockMvc
            .perform(
                get("/api/tracker/user/{userId}", otherUserId)
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    fun `createTracker should return created tracker`() {
        authenticateUser()
        val request =
            TrackerRequest(
                name = "New Tracker",
                icon = "💪",
                target = 1,
                unit = "раз",
                color = "SECONDARY",
                category = "fitness",
                weeklyTarget = 3,
                userId = userId,
            )
        val response =
            TrackerResponse(
                id = UUID.randomUUID(),
                userId = userId.toString(),
                name = request.name,
                icon = request.icon,
                target = request.target,
                unit = request.unit,
                color = request.color,
                category = request.category,
                weeklyTarget = request.weeklyTarget,
            )
        `when`(trackerService.createTracker(userId, request)).thenReturn(response)

        mockMvc
            .perform(
                post("/api/tracker")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf())
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(response.id.toString()))
            .andExpect(jsonPath("$.name").value("New Tracker"))
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    fun `deleteTracker should return no content`() {
        val trackerId = UUID.randomUUID()
        authenticateUser()
        `when`(trackerService.deleteTracker(authenticatedUser.id, trackerId)).thenAnswer { } // do nothing

        mockMvc
            .perform(
                delete("/api/tracker/{trackerId}", trackerId)
                    .with(csrf()),
            ).andExpect(status().isNoContent)
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    fun `saveProgress should return progress response`() {
        authenticateUser()
        val request =
            TrackerProgressRequest(
                trackerId = UUID.randomUUID(),
                date = "2026-05-05",
                value = 8,
                completed = true,
                userId = authenticatedUser.id,
            )
        val response =
            TrackerProgressResponse(
                id = UUID.randomUUID(),
                trackerId = request.trackerId,
                userId = authenticatedUser.id.toString(),
                date = request.date,
                value = request.value,
                completed = request.completed,
            )
        `when`(
            trackerService.saveProgress(
                authenticatedUser.id,
                request,
            ),
        ).thenReturn(response)

        mockMvc
            .perform(
                post("/api/tracker/progress")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf())
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.value").value(8))
            .andExpect(jsonPath("$.completed").value(true))
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    fun `getProgress should return progress list`() {
        authenticateUser()
        val userId = authenticatedUser.id
        val startDate = "2026-05-01"
        val endDate = "2026-05-07"
        val progress =
            TrackerProgressResponse(
                id = UUID.randomUUID(),
                trackerId = UUID.randomUUID(),
                userId = userId.toString(),
                date = "2026-05-05",
                value = 5,
                completed = false,
            )
        `when`(trackerService.getProgress(userId, startDate, endDate)).thenReturn(listOf(progress))

        mockMvc
            .perform(
                get("/api/tracker/progress/user/{userId}", userId)
                    .param("startDate", startDate)
                    .param("endDate", endDate)
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$[0].date").value("2026-05-05"))
            .andExpect(jsonPath("$[0].value").value(5))
    }
}
