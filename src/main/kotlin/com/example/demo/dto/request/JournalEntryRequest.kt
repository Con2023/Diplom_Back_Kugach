package com.example.demo.dto.request

data class JournalEntryRequest(
    val type: String,
    val date: String,
    val title: String,
    val content: String,
    val mood: String,
    val tags: List<String> = emptyList(),
)
