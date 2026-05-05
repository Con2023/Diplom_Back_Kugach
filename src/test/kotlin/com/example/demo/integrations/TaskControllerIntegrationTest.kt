package com.example.demo.integrations

import com.example.demo.configs.AuthenticatedUser
import com.example.demo.controller.TaskController
import com.example.demo.dto.request.TaskRequest
import com.example.demo.dto.response.TaskResponse
import com.example.demo.service.TaskService
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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import java.util.UUID

@WebMvcTest(TaskController::class)
class TaskControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var taskService: TaskService

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
    fun `save should create task and return 201`() {
        authenticateUser()
        val request =
            TaskRequest(
                userId = authenticatedUser.id,
                text = "New task",
            )
        val response =
            TaskResponse(
                id = UUID.randomUUID(),
                userId = authenticatedUser.id,
                text = "New task",
                completed = false,
            )
        `when`(taskService.save(request)).thenReturn(response)

        mockMvc
            .perform(
                post("/api/task/save")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.text").value("New task"))
    }

    @Test
    fun `update should modify task and return 200`() {
        authenticateUser()
        val taskId = UUID.randomUUID()
        val request =
            TaskRequest(
                id = taskId,
                userId = authenticatedUser.id,
                text = "Updated task",
                completed = true,
            )
        val response =
            TaskResponse(
                id = taskId,
                userId = authenticatedUser.id,
                text = "Updated task",
                completed = true,
            )
        `when`(taskService.update(request)).thenReturn(response)

        mockMvc
            .perform(
                post("/api/task/update")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.text").value("Updated task"))
            .andExpect(jsonPath("$.completed").value(true))
    }

    @Test
    fun `getTask should return task by id`() {
        authenticateUser()
        val taskId = UUID.randomUUID()
        val response =
            TaskResponse(
                id = taskId,
                userId = authenticatedUser.id,
                text = "My task",
                completed = false,
            )
        `when`(taskService.findById(taskId)).thenReturn(response)

        mockMvc
            .perform(
                get("/api/task/{taskId}", taskId)
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.text").value("My task"))
    }

    @Test
    fun `getTasks should return list of tasks for user and date`() {
        authenticateUser()
        val date = LocalDate.of(2026, 5, 5)
        val response =
            TaskResponse(
                id = UUID.randomUUID(),
                userId = authenticatedUser.id,
                text = "Task for date",
                completed = false,
            )
        `when`(taskService.findByUserIdAndDate(authenticatedUser.id, date)).thenReturn(listOf(response))

        mockMvc
            .perform(
                get("/api/task/user/{userId}", authenticatedUser.id)
                    .param("date", "2026-05-05")
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$[0].text").value("Task for date"))
    }

    @Test
    fun `delete should return no content`() {
        authenticateUser()
        val taskId = UUID.randomUUID()
        `when`(taskService.delete(taskId)).thenAnswer { }

        mockMvc
            .perform(
                delete("/api/task/{taskId}", taskId)
                    .with(csrf()),
            ).andExpect(status().isNoContent)
    }
}
