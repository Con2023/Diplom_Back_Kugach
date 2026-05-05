package com.example.demo.dto.mapper

import com.example.demo.dto.request.TrackerRequest
import com.example.demo.dto.response.TrackerResponse
import com.example.demo.repository.entity.TrackerEntity
import java.util.UUID

fun TrackerRequest.toEntity(userId: UUID): TrackerEntity =
    TrackerEntity(
        userId = userId,
        name = name,
        icon = icon,
        target = target,
        unit = unit,
        color = color,
        category = category,
        weeklyTarget = weeklyTarget,
    )

fun TrackerEntity.toResponse(): TrackerResponse =
    TrackerResponse(
        id = id,
        userId = userId.toString(),
        name = name,
        icon = icon,
        target = target,
        unit = unit,
        color = color,
        category = category,
        weeklyTarget = weeklyTarget,
    )
