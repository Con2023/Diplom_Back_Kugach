package com.example.demo.dto.request.auth

data class ResetPasswordRequest(
    val code: String,
    val newPassword: String,
)
