package com.example.demo.repository.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "refresh_tokens", schema = "public")
data class RefreshTokensEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
    @Column(name = "token_hash", nullable = false, unique = true, length = 512)
    val tokenHash: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: UserEntity,
    @Column(name = "expires_at", nullable = false)
    val expiresAt: LocalDateTime? = null,
    val device: String? = null,
    val ip: String? = null,
    @Column(name = "created_at")
    val createdAt: LocalDateTime? = null,
)
