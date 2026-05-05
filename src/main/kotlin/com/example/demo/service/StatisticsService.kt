package com.example.demo.service

import com.example.demo.dto.response.EmotionData
import com.example.demo.dto.response.StatisticsResponse
import com.example.demo.repository.JournalRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class StatisticsService(
    private val journalRepository: JournalRepository,
) {
    fun getStatistics(userId: UUID): StatisticsResponse {
        val journalEntries = journalRepository.findAllByUserId(userId)
        val moodCounts = journalEntries.groupBy { it.mood }

        val moodScore =
            if (journalEntries.isNotEmpty()) {
                val totalMoodValue: Int =
                    journalEntries.sumOf { entry ->
                        (
                            when (entry.mood) {
                                "HAPPY" -> 100
                                "NEUTRAL" -> 60
                                "SAD" -> 20
                                else -> 0
                            }
                        ) as Int
                    }
                totalMoodValue / journalEntries.size
            } else {
                0
            }

        val emotionData =
            listOf(
                EmotionData("Радость", moodCounts["HAPPY"]?.size ?: 0),
                EmotionData("Спокойствие", moodCounts["NEUTRAL"]?.size ?: 0),
                EmotionData("Грусть", moodCounts["SAD"]?.size ?: 0),
                EmotionData("Энергия", 0),
                EmotionData("Уверенность", 0),
                EmotionData("Мотивация", moodCounts["HAPPY"]?.size ?: 0),
            )

        return StatisticsResponse(
            moodScore = moodScore,
            emotionData = emotionData,
        )
    }
}
