package com.example.demo.dto.response

import com.example.demo.utils.Role
import jakarta.validation.constraints.Email
import java.time.LocalDate
import java.util.UUID

data class UserResponse(
    val id: UUID? = null,
    val firstName: String? = null,
    val secondName: String? = null,
    @Email
    val email: String,
    val age: Int? = null,
    val role: Role? = Role.ROLE_USER,
    val gender: String? = null,
    val weight: Double? = null,
    val targetWeight: Double? = null,
    val height: Double? = null,
    val image: String? = null,
    val birthDate: LocalDate? = null,
)
