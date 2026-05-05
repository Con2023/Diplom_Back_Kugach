package com.example.demo.controller

import com.example.demo.configs.AuthenticatedUser
import com.example.demo.dto.request.TrackerProgressRequest
import com.example.demo.dto.request.TrackerRequest
import com.example.demo.dto.response.TrackerProgressResponse
import com.example.demo.dto.response.TrackerResponse
import com.example.demo.service.TrackerService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@Tag(name = "Трекинг")
@RequestMapping("/api/tracker")
class TrackerController(
    private val trackerService: TrackerService,
) {
    @GetMapping("/user/{userId}")
    fun getUserTrackers(
        @PathVariable userId: UUID,
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
    ): List<TrackerResponse> {
        if (authenticatedUser.id != userId) {
            throw AccessDeniedException("Access denied")
        }
        return trackerService.getUserTrackers(userId)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createTracker(
        @RequestBody request: TrackerRequest,
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
    ): TrackerResponse = trackerService.createTracker(authenticatedUser.id, request)

    @DeleteMapping("/{trackerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTracker(
        @PathVariable trackerId: UUID,
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
    ) {
        trackerService.deleteTracker(authenticatedUser.id, trackerId)
    }

    @PostMapping("/progress")
    @ResponseStatus(HttpStatus.CREATED)
    fun saveProgress(
        @RequestBody request: TrackerProgressRequest,
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
    ): TrackerProgressResponse = trackerService.saveProgress(authenticatedUser.id, request)

    @GetMapping("/progress/user/{userId}")
    fun getProgress(
        @PathVariable userId: UUID,
        @RequestParam startDate: String,
        @RequestParam endDate: String,
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
    ): List<TrackerProgressResponse> {
        if (authenticatedUser.id != userId) {
            throw AccessDeniedException("Access denied")
        }
        return trackerService.getProgress(userId, startDate, endDate)
    }
}
