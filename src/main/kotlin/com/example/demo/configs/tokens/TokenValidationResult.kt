package com.example.demo.configs.tokens

import io.jsonwebtoken.Claims

sealed class TokenValidationResult {
    data class Valid(val claims: Claims) : TokenValidationResult()
    object Expired : TokenValidationResult()
    data class Invalid(val reason: String) : TokenValidationResult()
}
