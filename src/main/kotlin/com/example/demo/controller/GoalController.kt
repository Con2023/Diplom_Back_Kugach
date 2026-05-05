package com.example.demo.controller

import com.example.demo.configs.AuthenticatedUser
import com.example.demo.dto.request.GoalRequest
import com.example.demo.dto.response.GoalResponse
import com.example.demo.exception.AccessDeniedException
import com.example.demo.service.GoalService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@Tag(name = "Цели", description = "Управление целями")
@RequestMapping("/api/goal")
class GoalController(
    private val goalService: GoalService,
) {
    @PostMapping("/save")
    @ResponseStatus(HttpStatus.CREATED)
    fun save(
        @RequestBody request: GoalRequest,
    ): GoalResponse = goalService.save(request)

    @PostMapping("/update")
    fun update(
        @RequestBody request: GoalRequest,
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
    ): GoalResponse {
        if (authenticatedUser.id != request.userId) {
            throw AccessDeniedException("You can only access your own goals")
        }
        return goalService.update(request)
    }

    @GetMapping("/{goalId}")
    fun getGoal(
        @PathVariable goalId: UUID,
    ): GoalResponse = goalService.findById(goalId)

    @GetMapping("/user/{userId}")
    fun getAllGoals(
        @PathVariable userId: UUID,
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
    ): List<GoalResponse> {
        if (authenticatedUser.id != userId) {
            throw AccessDeniedException("You can only access your own goals")
        }
        return goalService.findAllByUser(userId)
    }

    @DeleteMapping("/{goalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable goalId: UUID,
    ) {
        goalService.delete(goalId)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(e: AccessDeniedException): ResponseEntity<String> =
        ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.message)
}
