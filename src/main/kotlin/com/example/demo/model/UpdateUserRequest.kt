package com.example.demo.model

import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UpdateUserRequest(
    @field:Size(min = 2, max = 50) val firstName: String?,
    @field:Size(min = 2, max = 50) val secondName: String?,
    @field:Min(0) @field:Max(150) val age: Int?,
    @field:Pattern(regexp = "Мужской|Женский|Не указано") val gender: String?,
    @field:DecimalMin("0.0") @field:DecimalMax("300.0") val weight: Double?,
    @field:DecimalMin("0.0") @field:DecimalMax("350.0") val height: Double?,
    @field:DecimalMin("0.0") val targetWeight: Double?,
)
