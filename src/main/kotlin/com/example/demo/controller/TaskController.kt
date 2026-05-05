package com.example.demo.controller

import com.example.demo.dto.request.TaskRequest
import com.example.demo.dto.response.TaskResponse
import com.example.demo.service.TaskService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.util.UUID

@RestController
@Tag(name = "Задачи", description = "Управление задачами")
@RequestMapping("/api/task")
class TaskController(
    private val taskService: TaskService,
) {
    @PostMapping("/save")
    @ResponseStatus(HttpStatus.CREATED)
    fun save(
        @RequestBody request: TaskRequest,
    ): TaskResponse = taskService.save(request)

    @PostMapping("/update")
    fun update(
        @RequestBody request: TaskRequest,
    ): TaskResponse = taskService.update(request)

    @GetMapping("/{taskId}")
    fun getTask(
        @PathVariable taskId: UUID,
    ): TaskResponse = taskService.findById(taskId)

    @GetMapping("/user/{userId}")
    fun getTasks(
        @PathVariable userId: UUID,
        @RequestParam date: LocalDate,
    ): List<TaskResponse> = taskService.findByUserIdAndDate(userId, date)

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable taskId: UUID,
    ) {
        taskService.delete(taskId)
    }
}
