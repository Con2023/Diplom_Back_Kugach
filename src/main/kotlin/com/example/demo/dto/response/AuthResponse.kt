package com.example.demo.dto.response

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserResponse,
)
