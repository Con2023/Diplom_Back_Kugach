package com.example.demo.configs.tokens

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.MessageDigest
import java.util.Base64

@Component
class TokenHasher(
    @Value("\${security.token-pepper}") private val pepper: String,
) {
    fun hash(token: String): String {
        val data = (token + pepper).toByteArray()
        val digest = MessageDigest.getInstance("SHA-256").digest(data)
        return Base64.getEncoder().encodeToString(digest)
    }
}
