package com.example.demo.model

data class ForgotPassword(
    val refreshToken: String,
    val email: String,
    val newPassword: String
)