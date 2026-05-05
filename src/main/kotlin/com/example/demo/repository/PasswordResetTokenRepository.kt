package com.example.demo.repository

import com.example.demo.repository.entity.PasswordResetTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime
import java.util.UUID

interface PasswordResetTokenRepository : JpaRepository<PasswordResetTokenEntity, UUID> {
    fun findByTokenHash(tokenHash: String): PasswordResetTokenEntity?

    fun findFirstByUserIdAndCreatedAtAfter(
        userId: UUID,
        minusMinutes: LocalDateTime,
    ): List<PasswordResetTokenEntity>
}
