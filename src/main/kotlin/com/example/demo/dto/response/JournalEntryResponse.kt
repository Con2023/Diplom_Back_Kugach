package com.example.demo.dto.response

import java.util.UUID

data class JournalEntryResponse(
    val id: UUID?,
    val userId: String,
    val type: String,
    val date: String,
    val title: String,
    val content: String,
    val mood: String,
    val tags: List<String>,
)
