package com.example.demo.controller

import com.example.demo.configs.AuthenticatedUser
import com.example.demo.dto.mapper.toUserResponse
import com.example.demo.dto.response.UserResponse
import com.example.demo.model.AvatarUploadResponse
import com.example.demo.model.UpdateUserRequest
import com.example.demo.service.UserService
import com.example.demo.utils.RateLimitingService
import com.example.demo.utils.data.RequestUtils
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException

@RestController
@Tag(name = "Пользователь", description = "Управление профилем пользователя")
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
    private val rateLimitingService: RateLimitingService,
) {
    @GetMapping("/me")
    @Operation(summary = "Получить информацию о текущем пользователе")
    fun getCurrentUser(
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
    ): ResponseEntity<UserResponse> {
        val user = userService.getUserById(authenticatedUser.id)
        return ResponseEntity.ok(user.toUserResponse())
    }

    @PutMapping("/me")
    @Operation(summary = "Обновить данные текущего пользователя")
    fun updateUser(
        @Valid @RequestBody request: UpdateUserRequest,
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
    ): ResponseEntity<UserResponse> {
        val ip = RequestUtils.getCurrentRequest()?.let { RequestUtils.getClientIp(it) }
        val key = "update:${authenticatedUser.id}:$ip"
        if (!rateLimitingService.tryConsume(key)) {
            throw ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many update attempts")
        }
        val updated = userService.updateUser(authenticatedUser.id, request)
        return ResponseEntity.ok(updated.toUserResponse())
    }

    @PostMapping("/me/avatar")
    @Operation(summary = "Загрузить аватар пользователя")
    fun uploadAvatar(
        @RequestParam("file") file: MultipartFile,
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
    ): ResponseEntity<AvatarUploadResponse> {
        val ip = RequestUtils.getCurrentRequest()?.let { RequestUtils.getClientIp(it) }
        val key = "avatar:${authenticatedUser.id}:$ip"
        if (!rateLimitingService.tryConsume(key)) {
            throw ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many avatar upload attempts")
        }
        val avatarUrl = userService.uploadAvatar(authenticatedUser.id, file)
        return ResponseEntity.ok(AvatarUploadResponse(avatarUrl))
    }
}
