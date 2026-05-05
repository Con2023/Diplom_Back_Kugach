package com.example.demo.repository

import com.example.demo.repository.entity.RefreshTokensEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TokenRepository : JpaRepository<RefreshTokensEntity, UUID> {
    fun findByTokenHash(hash: String): RefreshTokensEntity?

    fun deleteByUserId(userId: UUID)
}
