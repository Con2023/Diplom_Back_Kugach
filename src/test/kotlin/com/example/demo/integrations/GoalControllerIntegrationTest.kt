package com.example.demo.integrations

import com.example.demo.configs.AuthenticatedUser
import com.example.demo.controller.GoalController
import com.example.demo.dto.request.GoalRequest
import com.example.demo.dto.response.GoalResponse
import com.example.demo.service.GoalService
import com.example.demo.utils.Role
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@WebMvcTest(GoalController::class)
class GoalControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var goalService: GoalService

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
    fun `save goal should return created goal`() {
        authenticateUser()
        val request =
            GoalRequest(
                userId = userId,
                text = "New goal",
            )
        val response =
            GoalResponse(
                id = UUID.randomUUID(),
                userId = userId,
                text = "New goal",
                tasks = emptyList(),
            )
        `when`(goalService.save(request)).thenReturn(response)

        mockMvc
            .perform(
                post("/api/goal/save")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.text").value("New goal"))
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    fun `update goal should return updated goal when user is owner`() {
        authenticateUser()
        val goalId = UUID.randomUUID()
        val request =
            GoalRequest(
                id = goalId,
                userId = userId,
                text = "Updated goal",
            )
        val response =
            GoalResponse(
                id = goalId,
                userId = userId,
                text = "Updated goal",
                tasks = emptyList(),
            )
        `when`(goalService.update(request)).thenReturn(response)

        mockMvc
            .perform(
                post("/api/goal/update")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.text").value("Updated goal"))
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    fun `update goal should return forbidden when user is not owner`() {
        authenticateUser()
        val otherUserId = UUID.randomUUID()
        val request =
            GoalRequest(
                id = UUID.randomUUID(),
                userId = otherUserId,
                text = "You can only access your own goals",
            )
        mockMvc
            .perform(
                post("/api/goal/update")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    fun `get goal should return goal by id`() {
        authenticateUser()
        val goalId = UUID.randomUUID()
        val response =
            GoalResponse(
                id = goalId,
                userId = userId,
                text = "My goal",
                tasks = emptyList(),
            )
        `when`(goalService.findById(goalId)).thenReturn(response)

        mockMvc
            .perform(
                get("/api/goal/{goalId}", goalId)
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.text").value("My goal"))
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    fun `get all goals should return list for authorized user`() {
        authenticateUser()
        val response =
            GoalResponse(
                id = UUID.randomUUID(),
                userId = userId,
                text = "Goal 1",
                tasks = emptyList(),
            )
        `when`(goalService.findAllByUser(userId)).thenReturn(listOf(response))

        mockMvc
            .perform(
                get("/api/goal/user/{userId}", userId)
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$[0].text").value("Goal 1"))
    }

    @Test
    fun `get all goals should return unauthorized when not authenticated`() {
        val someUserId = UUID.randomUUID()
        mockMvc
            .perform(
                get("/api/goal/user/{userId}", someUserId)
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    fun `get all goals should return forbidden when user id does not match`() {
        authenticateUser()
        val otherUserId = UUID.randomUUID()
        mockMvc
            .perform(
                get("/api/goal/user/{userId}", otherUserId)
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    fun `delete goal should return no content`() {
        authenticateUser()
        val goalId = UUID.randomUUID()
        `when`(goalService.delete(goalId)).thenAnswer { }

        mockMvc
            .perform(
                delete("/api/goal/{goalId}", goalId)
                    .with(csrf()),
            ).andExpect(status().isNoContent)
    }
}
