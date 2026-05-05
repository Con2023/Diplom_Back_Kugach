package com.example.demo.dto.request.auth

import jakarta.validation.constraints.Email

data class LoginRequest(
    @Email
    val email: String,
    val password: String,
)
