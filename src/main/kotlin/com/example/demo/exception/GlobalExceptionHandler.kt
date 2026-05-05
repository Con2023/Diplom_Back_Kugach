package com.yourpackage.exception

import com.example.demo.exception.InvalidTokenException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

    data class ErrorResponse(
        val timestamp: LocalDateTime = LocalDateTime.now(),
        val status: Int,
        val error: String,
        val message: String?,
        val path: String?
    )

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(
        ex: ResponseStatusException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val status = ex.statusCode
        val response = ErrorResponse(
            status = status.value(),
            error = status.toString(),
            message = ex.reason,
            path = request.requestURI
        )
        return ResponseEntity.status(status).body(response)
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentials(
        ex: BadCredentialsException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            status = HttpStatus.UNAUTHORIZED.value(),
            error = HttpStatus.UNAUTHORIZED.reasonPhrase,
            message = ex.message ?: "Invalid credentials",
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response)
    }

    @ExceptionHandler(DisabledException::class)
    fun handleDisabled(
        ex: DisabledException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            status = HttpStatus.FORBIDDEN.value(),
            error = HttpStatus.FORBIDDEN.reasonPhrase,
            message = ex.message ?: "User account is disabled",
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response)
    }

    @ExceptionHandler(InvalidTokenException::class)
    fun handleInvalidToken(
        ex: InvalidTokenException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            status = HttpStatus.UNAUTHORIZED.value(),
            error = HttpStatus.UNAUTHORIZED.reasonPhrase,
            message = ex.message,
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors
            .joinToString("; ") { "${it.field}: ${it.defaultMessage}" }
        val response = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = "Validation failed: $errors",
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
            message = "An unexpected error occurred",
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }
}