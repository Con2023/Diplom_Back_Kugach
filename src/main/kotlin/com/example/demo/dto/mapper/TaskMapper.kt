package com.example.demo.dto.mapper

import com.example.demo.dto.request.TaskRequest
import com.example.demo.dto.response.TaskResponse
import com.example.demo.repository.entity.TaskEntity

fun TaskRequest.toEntity(): TaskEntity =
    TaskEntity(
        id = id,
        userId = userId,
        text = text,
        description = description,
        completed = completed,
        date = date,
    )

fun TaskEntity.toResponse(): TaskResponse =
    TaskResponse(
        id = id,
        userId = userId,
        text = text,
        description = description,
        completed = completed,
        date = date,
    )
