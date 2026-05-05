package com.example.demo.repository.mapper

import com.example.demo.model.ForgotPassword
import com.example.demo.model.RegisterUser
import com.example.demo.model.User
import com.example.demo.repository.entity.UserEntity
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime

fun UserEntity.toDomain(): User =
    User(
        id = id,
        firstName = firstName,
        secondName = secondName,
        email = email,
        age = age,
        weight = weight,
        role = role,
        height = height,
        targetWeight = targetWeight,
        gender = gender,
        image = image,
        birthDate = birthDate,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun User.toEntity(encodedPassword: String): UserEntity =
    UserEntity(
        email = email,
        firstName = firstName,
        secondName = secondName,
        password = encodedPassword,
        createdAt = LocalDateTime.now(),
    )

fun RegisterUser.toUserEntity(passwordEncoder: PasswordEncoder): UserEntity =
    UserEntity(
        email = this.email,
        firstName = this.firstName,
        secondName = this.secondName,
        password = passwordEncoder.encode(this.password),
        createdAt = LocalDateTime.now(),
    )

fun ForgotPassword.toUserEntity(passwordEncoder: PasswordEncoder): UserEntity =
    UserEntity(
        email = this.email,
        password = passwordEncoder.encode(this.newPassword),
        createdAt = LocalDateTime.now(),
    )
