package com.example.demo.model

import com.example.demo.utils.Role
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class User(
    var id: UUID? = null,
    val firstName: String? = null,
    val secondName: String? = null,
    val email: String,
    val age: Int? = null,
    val role: Role? = Role.ROLE_USER,
    val enabled: Boolean = true,
    val gender: String? = null,
    val weight: Double? = null,
    val height: Double? = null,
    val targetWeight: Double? = null,
    val birthDate: LocalDate? = null,
    val image: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)
