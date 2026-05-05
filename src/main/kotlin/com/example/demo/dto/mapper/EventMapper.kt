package com.example.demo.dto.mapper

import com.example.demo.dto.request.EventRequest
import com.example.demo.dto.response.EventResponse
import com.example.demo.repository.entity.EventEntity

fun EventRequest.toEntity(): EventEntity =
    EventEntity(
        id = id,
        userId = userId,
        text = text,
        startTime = startTime,
        endTime = endTime,
        date = date,
        completed = completed,
    )

fun EventEntity.toResponse(): EventResponse =
    EventResponse(
        id = id,
        userId = userId,
        text = text,
        startTime = startTime,
        endTime = endTime,
        date = date,
        completed = completed,
    )
