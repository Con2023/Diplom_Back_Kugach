package com.example.demo.units

import com.example.demo.repository.JournalRepository
import com.example.demo.repository.entity.JournalEntity
import com.example.demo.service.StatisticsService
import io.mockk.every
import io.mockk.mockk
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StatisticsServiceTest {
    private val journalRepository = mockk<JournalRepository>(relaxed = true)
    private val service = StatisticsService(journalRepository)

    @Test
    fun `getStatistics should return default values when no journal entries exist`() {
        val userId = UUID.randomUUID()
        every { journalRepository.findAllByUserId(userId) } returns emptyList()

        val result = service.getStatistics(userId)

        assertEquals(0, result.moodScore)
        assertTrue(result.emotionData.all { it.value == 0 })
    }

    @Test
    fun `getStatistics should compute correct moodScore and emotion counts`() {
        val userId = UUID.randomUUID()
        val entries =
            listOf(
                JournalEntity(
                    id = UUID.randomUUID(),
                    userId = userId,
                    type = "EMOTIONS",
                    date = "2026-05-05",
                    title = "",
                    content = "",
                    mood = "HAPPY",
                    tags = emptyList(),
                ),
                JournalEntity(
                    id = UUID.randomUUID(),
                    userId = userId,
                    type = "EMOTIONS",
                    date = "2026-05-05",
                    title = "",
                    content = "",
                    mood = "HAPPY",
                    tags = emptyList(),
                ),
                JournalEntity(
                    id = UUID.randomUUID(),
                    userId = userId,
                    type = "EMOTIONS",
                    date = "2026-05-05",
                    title = "",
                    content = "",
                    mood = "NEUTRAL",
                    tags = emptyList(),
                ),
                JournalEntity(
                    id = UUID.randomUUID(),
                    userId = userId,
                    type = "EMOTIONS",
                    date = "2026-05-05",
                    title = "",
                    content = "",
                    mood = "SAD",
                    tags = emptyList(),
                ),
            )
        every { journalRepository.findAllByUserId(userId) } returns entries

        val result = service.getStatistics(userId)
        // totalMoodValue = 100 + 100 + 60 + 20 = 280, size = 4 → moodScore = 70
        assertEquals(70, result.moodScore)
        // Check emotionData values
        assertEquals(2, result.emotionData.find { it.emotion == "Радость" }?.value)
        assertEquals(1, result.emotionData.find { it.emotion == "Спокойствие" }?.value)
        assertEquals(1, result.emotionData.find { it.emotion == "Грусть" }?.value)
        assertEquals(0, result.emotionData.find { it.emotion == "Энергия" }?.value)
        assertEquals(0, result.emotionData.find { it.emotion == "Уверенность" }?.value)
        assertEquals(2, result.emotionData.find { it.emotion == "Мотивация" }?.value) // тоже HAPPY
    }
}
