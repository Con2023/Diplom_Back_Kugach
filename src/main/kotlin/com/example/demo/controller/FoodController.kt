package com.example.demo.controller

import com.example.demo.configs.AuthenticatedUser
import com.example.demo.dto.request.FoodEntryRequest
import com.example.demo.dto.response.FoodEntryResponse
import com.example.demo.exception.AccessDeniedException
import com.example.demo.service.FoodService
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
@Tag(name = "Дневник питания", description = "Трекинг питания")
@RequestMapping("/api/food")
class FoodController(
    private val foodService: FoodService,
) {
    @GetMapping("/user/{userId}")
    fun getUserEntries(
        @PathVariable userId: UUID,
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
    ): List<FoodEntryResponse> {
        if (authenticatedUser.id != userId) {
            throw AccessDeniedException("You can only access your own dairy")
        }
        return foodService.getUserEntries(userId)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createEntry(
        @RequestBody request: FoodEntryRequest,
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
    ): FoodEntryResponse {
        if (authenticatedUser.id != request.userId) {
            throw AccessDeniedException("You can only access your own dairy")
        }
        return foodService.createEntry(authenticatedUser.id, request)
    }

    @DeleteMapping("/{entryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteEntry(
        @PathVariable entryId: UUID,
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
    ) {
        foodService.deleteEntry(authenticatedUser.id, entryId)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(e: AccessDeniedException): ResponseEntity<String> =
        ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.message)
}
