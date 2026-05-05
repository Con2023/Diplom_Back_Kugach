package com.example.demo.dto.request.auth

import jakarta.validation.constraints.Email

data class RegisterRequest(
    val firstName: String,
    val secondName: String,
    @Email
    val email: String,
    val password: String,
)
