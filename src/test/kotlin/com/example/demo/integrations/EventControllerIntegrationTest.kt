package com.example.demo.integrations

import com.example.demo.configs.AuthenticatedUser
import com.example.demo.controller.EventController
import com.example.demo.dto.request.EventRequest
import com.example.demo.dto.response.EventResponse
import com.example.demo.service.EventService
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@WebMvcTest(EventController::class)
class EventControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var eventService: EventService

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
    fun `getUserEvents should return list for owner`() {
        authenticateUser()
        val userId = authenticatedUser.id
        val response =
            EventResponse(
                id = UUID.randomUUID(),
                userId = userId,
                text = "Morning run",
                startTime = "07:00",
                endTime = "08:00",
                date = "2026-05-05",
                completed = false,
            )
        `when`(eventService.findAllByUser(userId)).thenReturn(listOf(response))

        mockMvc
            .perform(
                get("/api/event/user/{userId}", userId)
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$[0].text").value("Morning run"))
    }

    @Test
    fun `getUserEvents should return unauthorized when not authenticated`() {
        val someUserId = UUID.randomUUID()
        mockMvc
            .perform(
                get("/api/event/user/{userId}", someUserId)
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    fun `getUserEvents should return forbidden when user id does not match`() {
        authenticateUser()
        val otherUserId = UUID.randomUUID()
        mockMvc
            .perform(
                get("/api/event/user/{userId}", otherUserId)
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    fun `createEvent should return created event`() {
        authenticateUser()
        val request =
            EventRequest(
                userId = authenticatedUser.id,
                text = "Yoga",
                startTime = "09:00",
                endTime = "10:00",
                date = "2026-05-06",
                completed = false,
            )
        val response =
            EventResponse(
                id = UUID.randomUUID(),
                userId = authenticatedUser.id,
                text = "Yoga",
                startTime = "09:00",
                endTime = "10:00",
                date = "2026-05-06",
                completed = false,
            )
        `when`(eventService.save(request)).thenReturn(response)

        mockMvc
            .perform(
                post("/api/event/save")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.text").value("Yoga"))
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    fun `createEvent should return forbidden when user id does not match`() {
        authenticateUser()
        val otherUserId = UUID.randomUUID()
        val request =
            EventRequest(
                userId = otherUserId,
                text = "Unauthorized event",
                date = "",
                startTime = "",
            )
        mockMvc
            .perform(
                post("/api/event/save")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isForbidden)
    }

    @Test
    fun `updateEvent should return updated event`() {
        authenticateUser()
        val request =
            EventRequest(
                id = UUID.randomUUID(),
                userId = authenticatedUser.id,
                text = "Updated event",
                completed = true,
                date = "",
                startTime = "",
            )
        val response =
            EventResponse(
                id = request.id,
                userId = authenticatedUser.id,
                text = "Updated event",
                completed = true,
                date = "",
                startTime = "",
            )
        `when`(eventService.update(request)).thenReturn(response)

        mockMvc
            .perform(
                put("/api/event/update")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.text").value("Updated event"))
            .andExpect(jsonPath("$.completed").value(true))
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    fun `updateEvent should return forbidden when user id does not match`() {
        authenticateUser()
        val otherUserId = UUID.randomUUID()
        val request =
            EventRequest(
                id = UUID.randomUUID(),
                userId = otherUserId,
                text = "Should fail",
                date = "",
                startTime = "",
            )
        mockMvc
            .perform(
                put("/api/event/update")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    fun `deleteEvent should return no content`() {
        authenticateUser()
        val eventId = UUID.randomUUID()
        `when`(eventService.delete(eventId)).thenAnswer { } // void

        mockMvc
            .perform(
                delete("/api/event/{eventId}", eventId)
                    .with(csrf()),
            ).andExpect(status().isNoContent)
    }
}
