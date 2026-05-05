package com.example.demo.repository

import com.example.demo.repository.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository : JpaRepository<UserEntity, UUID> {
    fun findByEmail(email: String): UserEntity?
}
