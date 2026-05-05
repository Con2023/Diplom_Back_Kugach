package com.example.demo.dto.request.auth

import jakarta.validation.constraints.Email

data class ForgotPasswordRequest(
    @Email
    val email: String,
)
