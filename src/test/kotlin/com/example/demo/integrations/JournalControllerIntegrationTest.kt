package com.example.demo.controller

import com.example.demo.configs.AuthenticatedUser
import com.example.demo.dto.request.JournalEntryRequest
import com.example.demo.dto.response.JournalEntryResponse
import com.example.demo.service.JournalService
import com.example.demo.utils.Role
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@WebMvcTest(JournalController::class)
class JournalControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var journalService: JournalService

    private val userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
    private val otherUserId = UUID.fromString("223e4567-e89b-12d3-a456-426614174001")
    private val entryId = UUID.fromString("323e4567-e89b-12d3-a456-426614174002")

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
    fun `getUserEntries should return entries when user accesses own entries`() {
        authenticateUser()

        val entryResponse =
            JournalEntryResponse(
                id = UUID.randomUUID(),
                userId = userId.toString(),
                title = "My day",
                content = "Good day",
                mood = "happy",
                date = "",
                tags = listOf(""),
                type = "",
            )
        Mockito
            .`when`(journalService.getUserEntries(userId))
            .thenReturn(listOf(entryResponse))

        mockMvc
            .perform(get("/api/journal/user/{userId}", userId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].title").value("My day"))
            .andExpect(jsonPath("$[0].content").value("Good day"))
    }

    @Test
    fun `getUserEntries should return forbidden when user tries to access other user's entries`() {
        authenticateUser()
        mockMvc
            .perform(get("/api/journal/user/{userId}", otherUserId))
            .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    fun `createEntry should return created entry when authenticated`() {
        authenticateUser()
        val request =
            JournalEntryRequest(
                title = "New entry",
                content = "Content",
                mood = "excited",
                date = "",
                tags = listOf(""),
                type = "",
            )
        val response =
            JournalEntryResponse(
                id = UUID.randomUUID(),
                userId = userId.toString(),
                title = request.title,
                content = request.content,
                mood = request.mood,
                date = "",
                tags = listOf(""),
                type = "",
            )
        Mockito
            .`when`(journalService.createEntry(userId, request))
            .thenReturn(response)

        mockMvc
            .perform(
                post("/api/journal")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf())
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.title").value("New entry"))
            .andExpect(jsonPath("$.content").value("Content"))
    }

    @Test
    fun `createEntry should return unauthorized when not authenticated`() {
        val request =
            JournalEntryRequest(
                title = "New entry",
                content = "Content",
                mood = "excited",
                date = "",
                tags = listOf(""),
                type = "",
            )
        mockMvc
            .perform(
                post("/api/journal")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf())
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    fun `updateEntry should return updated entry when authenticated and entry belongs to user`() {
        authenticateUser()
        val request =
            JournalEntryRequest(
                title = "Updated title",
                content = "Updated content",
                mood = "calm",
                date = "",
                tags = listOf(""),
                type = "",
            )
        val response =
            JournalEntryResponse(
                id = entryId,
                userId = userId.toString(),
                title = request.title,
                content = request.content,
                mood = request.mood,
                date = "",
                tags = listOf(""),
                type = "",
            )
        Mockito
            .`when`(journalService.updateEntry(userId, entryId, request))
            .thenReturn(response)

        mockMvc
            .perform(
                put("/api/journal/{id}", entryId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf())
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Updated title"))
            .andExpect(jsonPath("$.content").value("Updated content"))
    }

    @Test
    fun `updateEntry should return unauthorized when not authenticated`() {
        val request =
            JournalEntryRequest(
                title = "Updated title",
                content = "Updated content",
                mood = "calm",
                date = "",
                tags = listOf(""),
                type = "",
            )
        mockMvc
            .perform(
                put("/api/journal/{id}", entryId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf())
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `deleteEntry should return no content when authenticated`() {
        authenticateUser()
        Mockito.doNothing().`when`(journalService).deleteEntry(userId, entryId)

        mockMvc
            .perform(delete("/api/journal/{id}", entryId).with(csrf()))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `deleteEntry should return unauthorized when not authenticated`() {
        mockMvc
            .perform(delete("/api/journal/{id}", entryId).with(csrf()))
            .andExpect(status().isUnauthorized)
    }
}
