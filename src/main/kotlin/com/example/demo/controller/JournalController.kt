package com.example.demo.controller

// com.example.demo.controller.JournalController.kt
import com.example.demo.configs.AuthenticatedUser
import com.example.demo.dto.request.JournalEntryRequest
import com.example.demo.dto.response.JournalEntryResponse
import com.example.demo.service.JournalService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
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
@Tag(name = "Дневники", description = "Управление дневниками")
@RequestMapping("/api/journal")
class JournalController(
    private val journalService: JournalService,
) {
    @GetMapping("/user/{userId}")
    fun getUserEntries(
        @PathVariable userId: UUID,
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
    ): List<JournalEntryResponse> {
        if (authenticatedUser.id != userId) {
            throw AccessDeniedException("You can only access your own journal entries")
        }
        return journalService.getUserEntries(userId)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createEntry(
        @RequestBody request: JournalEntryRequest,
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
    ): JournalEntryResponse = journalService.createEntry(authenticatedUser.id, request)

    @PutMapping("/{id}")
    fun updateEntry(
        @PathVariable id: UUID,
        @RequestBody request: JournalEntryRequest,
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
    ): JournalEntryResponse = journalService.updateEntry(authenticatedUser.id, id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteEntry(
        @PathVariable id: UUID,
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
    ) {
        journalService.deleteEntry(authenticatedUser.id, id)
    }
}
