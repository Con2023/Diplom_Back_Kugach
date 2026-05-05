package com.example.demo.controller

import com.example.demo.configs.AuthenticatedUser
import com.example.demo.dto.mapper.toAuthResponse
import com.example.demo.dto.mapper.toLoginUser
import com.example.demo.dto.mapper.toRegisterUser
import com.example.demo.dto.request.auth.ForgotPasswordRequest
import com.example.demo.dto.request.auth.LoginRequest
import com.example.demo.dto.request.auth.LogoutRequest
import com.example.demo.dto.request.auth.RegisterRequest
import com.example.demo.dto.request.auth.ResetPasswordRequest
import com.example.demo.dto.request.tokens.RefreshRequest
import com.example.demo.dto.response.AuthResponse
import com.example.demo.service.AuthService
import com.example.demo.utils.RateLimitingService
import com.example.demo.utils.data.RequestUtils
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@Tag(name = "Аутентификация", description = "Регистрация, вход и управление токенами")
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val rateLimitingService: RateLimitingService,
) {
    @PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя")
    fun register(
        @Valid @RequestBody request: RegisterRequest,
    ): ResponseEntity<AuthResponse> {
        val result = authService.register(request.toRegisterUser())
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(result.toAuthResponse())
    }

    @PostMapping("/login")
    @Operation(summary = "Авторизация пользователя")
    fun login(
        @Valid @RequestBody request: LoginRequest,
    ): ResponseEntity<AuthResponse> {
        val ip = RequestUtils.getCurrentRequest()?.let { RequestUtils.getClientIp(it) }
        val key = "login:${request.email}:$ip"
        if (!rateLimitingService.tryConsume(key)) {
            throw ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many login attempts")
        }
        val result = authService.login(request.toLoginUser())
        return ResponseEntity.ok(result.toAuthResponse())
    }

    @PostMapping("/refresh")
    @Operation(summary = "Пересоздание токенов")
    fun refresh(
        @Valid @RequestBody request: RefreshRequest,
    ): ResponseEntity<AuthResponse> {
        val result = authService.refreshToken(request.refreshToken)
        return ResponseEntity.ok(result.toAuthResponse())
    }

    @PostMapping("/logout")
    @Operation(summary = "Выход из приложения")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logout(
        @Valid @RequestBody request: LogoutRequest,
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
    ) {
        authService.logout(request.refreshToken, authenticatedUser.id)
    }

    @PostMapping("/logoutAll")
    @Operation(summary = "Выход со всех устройств")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logoutAll(
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
    ) {
        authService.revokeAllUserTokens(authenticatedUser.id)
    }

    @PostMapping("/forgotPassword")
    @Operation(summary = "Смена пароля(проверка пользователя и удаление старого токена)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun forgotPassword(
        @Valid @RequestBody request: ForgotPasswordRequest,
    ) {
        val ip = RequestUtils.getCurrentRequest()?.let { RequestUtils.getClientIp(it) }
        val key = "login:${request.email}:$ip"
        if (!rateLimitingService.tryConsume(key)) {
            Thread.sleep(2000)
            return
        }
        authService.initiatePasswordReset(request.email)
    }

    @PostMapping("/resetPassword")
    @Operation(summary = "Смена пароля(сохранение нового пароля и выход со всех устройств)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun resetPassword(
        @Valid @RequestBody request: ResetPasswordRequest,
    ) {
        authService.resetPassword(request.code, request.newPassword)
    }
}
