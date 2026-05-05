package com.example.demo.dto.mapper

import com.example.demo.dto.request.GoalRequest
import com.example.demo.dto.response.GoalResponse
import com.example.demo.repository.entity.GoalEntity

fun GoalRequest.toEntity(): GoalEntity =
    GoalEntity(
        id = id,
        userId = userId,
        text = text,
        category = category,
        priority = priority,
        deadline = deadline,
        tasks = tasks.map { it.toEntity() }.toMutableList(),
    ).apply {
        tasks.forEach { it.goal = this }
    }

fun GoalEntity.toResponse(): GoalResponse =
    GoalResponse(
        id = id,
        userId = userId,
        text = text,
        category = category,
        priority = priority,
        deadline = deadline,
        tasks = tasks.map { it.toResponse() },
    )
