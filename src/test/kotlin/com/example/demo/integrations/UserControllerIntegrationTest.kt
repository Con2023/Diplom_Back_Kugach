package com.example.demo.integrations

import com.example.demo.configs.AuthenticatedUser
import com.example.demo.controller.UserController
import com.example.demo.dto.response.UserResponse
import com.example.demo.model.User
import com.example.demo.service.UserService
import com.example.demo.utils.RateLimitingService
import com.example.demo.utils.Role
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@WebMvcTest(UserController::class)
class UserControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var userService: UserService

    @MockitoBean
    private lateinit var rateLimitingService: RateLimitingService

    private val authenticatedUser =
        AuthenticatedUser(
            id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
            email = "test@example.com",
            roles = setOf(Role.ROLE_USER),
        )

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
    fun `getCurrentUser should return user profile`() {
        authenticateUser()
        val userResponse =
            UserResponse(
                id = authenticatedUser.id,
                email = "test@example.com",
                firstName = "Test",
                secondName = "User",
            )
        val user =
            User(
                id = authenticatedUser.id,
                email = "test@example.com",
                firstName = "Test",
                secondName = "User",
            )
        `when`(userService.getUserById(authenticatedUser.id)).thenReturn(user)

        mockMvc
            .perform(
                get("/api/users/me")
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(authenticatedUser.id.toString()))
            .andExpect(jsonPath("$.email").value("test@example.com"))
    }

    @Test
    fun `uploadAvatar should return 429 when rate limit exceeded`() {
        authenticateUser()
        val file =
            MockMultipartFile(
                "file",
                "avatar.jpg",
                "image/jpeg",
                "fake-image-content".toByteArray(),
            )
        `when`(rateLimitingService.tryConsume(" ")).thenReturn(false)

        mockMvc
            .perform(
                multipart("/api/users/me/avatar")
                    .file(file)
                    .with(csrf()),
            ).andExpect(status().isTooManyRequests)
    }
}
