package com.example.demo.controller

import com.example.demo.configs.AuthenticatedUser
import com.example.demo.dto.request.EventRequest
import com.example.demo.dto.response.EventResponse
import com.example.demo.exception.AccessDeniedException
import com.example.demo.service.EventService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@Tag(name = "События", description = "Управление событиями")
@RequestMapping("/api/event")
class EventController(
    private val eventService: EventService,
) {
    @GetMapping("/user/{userId}")
    @Operation(summary = "Получить список событий пользователя")
    fun getUserEvents(
        @PathVariable userId: UUID,
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
    ): List<EventResponse> {
        if (authenticatedUser.id != userId) {
            throw AccessDeniedException("You can only access your own events")
        }
        return eventService.findAllByUser(userId)
    }

    @PostMapping("/save")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать новое событие")
    fun createEvent(
        @RequestBody request: EventRequest,
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
    ): EventResponse {
        if (authenticatedUser.id != request.userId) {
            throw AccessDeniedException("You can only access your own events")
        }
        return eventService.save(request)
    }

    @PutMapping("/update")
    @Operation(summary = "Обновить существующее событие")
    fun updateEvent(
        @RequestBody request: EventRequest,
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
    ): EventResponse {
        if (authenticatedUser.id != request.userId) {
            throw AccessDeniedException("You can only access your own events")
        }
        return eventService.update(request)
    }

    @DeleteMapping("/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить событие")
    fun deleteEvent(
        @PathVariable eventId: UUID,
    ) = eventService.delete(eventId)

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(e: AccessDeniedException): ResponseEntity<String> =
        ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.message)
}
