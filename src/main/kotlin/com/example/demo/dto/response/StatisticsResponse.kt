package com.example.demo.dto.response

data class StatisticsResponse(
    val moodScore: Int,
    val emotionData: List<EmotionData>,
)

data class EmotionData(
    val emotion: String,
    val value: Int,
)
