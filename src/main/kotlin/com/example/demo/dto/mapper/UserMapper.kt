package com.example.demo.dto.mapper

import com.example.demo.dto.response.UserResponse
import com.example.demo.model.User

fun User.toUserResponse(): UserResponse =
    UserResponse(
        id = this.id,
        firstName = this.firstName,
        secondName = this.secondName,
        email = this.email,
        age = this.age,
        gender = this.gender,
        weight = this.weight,
        height = this.height,
        targetWeight = this.targetWeight,
        image = this.image,
    )
