package com.example.demo.repository.mapper

import com.example.demo.configs.tokens.TokenHasher
import com.example.demo.repository.entity.RefreshTokensEntity
import com.example.demo.repository.entity.UserEntity
import java.time.LocalDateTime

fun UserEntity.toRefreshTokenEntity(
    rawToken: String,
    tokenHasher: TokenHasher,
    device: String? = null,
    ip: String? = null,
): RefreshTokensEntity {
    val tokenHash = tokenHasher.hash(rawToken)
    return RefreshTokensEntity(
        tokenHash = tokenHash,
        user = this,
        device = device,
        ip = ip,
        expiresAt = LocalDateTime.now().plusDays(30),
        createdAt = LocalDateTime.now(),
    )
}
