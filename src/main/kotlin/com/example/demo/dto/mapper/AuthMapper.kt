package com.example.demo.dto.mapper

import com.example.demo.dto.request.auth.LoginRequest
import com.example.demo.dto.request.auth.RegisterRequest
import com.example.demo.dto.response.AuthResponse
import com.example.demo.dto.response.UserResponse
import com.example.demo.model.AuthResult
import com.example.demo.model.LoginUser
import com.example.demo.model.RegisterUser
import com.example.demo.model.User

fun RegisterRequest.toRegisterUser() =
    RegisterUser(
        email = email,
        firstName = firstName,
        secondName = secondName,
        password = password,
    )

fun LoginRequest.toLoginUser() =
    LoginUser(
        email = email,
        password = password,
    )

fun User.toResponse() =
    UserResponse(
        id = id,
        firstName = firstName,
        secondName = secondName,
        email = email,
        age = age,
        role = role,
        weight = weight,
        targetWeight = targetWeight,
        gender = gender,
        height = height,
        image = image,
        birthDate = birthDate,
    )

fun AuthResult.toAuthResponse() =
    AuthResponse(
        accessToken = accessToken,
        refreshToken = refreshToken,
        user = user,
    )
