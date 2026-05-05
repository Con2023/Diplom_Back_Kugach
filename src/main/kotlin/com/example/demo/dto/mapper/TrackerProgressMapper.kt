package com.example.demo.dto.mapper

import com.example.demo.dto.request.TrackerProgressRequest
import com.example.demo.dto.response.TrackerProgressResponse
import com.example.demo.repository.entity.TrackerProgressEntity
import java.util.UUID

fun TrackerProgressRequest.toEntity(userId: UUID): TrackerProgressEntity =
    TrackerProgressEntity(
        trackerId = trackerId,
        userId = userId,
        date = date,
        value = value,
        completed = completed,
    )

fun TrackerProgressEntity.toResponse(): TrackerProgressResponse =
    TrackerProgressResponse(
        id = id,
        trackerId = trackerId,
        userId = userId.toString(),
        date = date,
        value = value,
        completed = completed,
    )
