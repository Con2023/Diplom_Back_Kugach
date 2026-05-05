package com.example.demo.model

import com.example.demo.dto.response.UserResponse

data class AuthResult(
    val accessToken: String,
    val refreshToken: String,
    val user: UserResponse,
)
