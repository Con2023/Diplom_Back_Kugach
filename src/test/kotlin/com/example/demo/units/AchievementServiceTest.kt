package com.example.demo.units

import com.example.demo.repository.UserAchievementRepository
import com.example.demo.repository.UserProgressRepository
import com.example.demo.repository.entity.UserAchievementEntity
import com.example.demo.repository.entity.UserProgressEntity
import com.example.demo.service.AchievementService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import java.util.Optional
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AchievementServiceTest {
    private lateinit var userProgressRepo: UserProgressRepository
    private lateinit var userAchievementRepo: UserAchievementRepository
    private lateinit var service: AchievementService

    private val userId = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        userProgressRepo = mockk()
        userAchievementRepo = mockk()
        service = AchievementService(userProgressRepo, userAchievementRepo)
    }

    @Test
    fun `checkAndUnlock should create progress when not exists and not unlock anything`() {
        every { userProgressRepo.findByUserId(userId) } returns null
        every { userAchievementRepo.findByUserIdAndAchievementId(userId, any()) } returns null
        every { userProgressRepo.save(any()) } answers { firstArg() }
        every { userAchievementRepo.save(any()) } answers { firstArg() }

        service.checkAndUnlock(userId)

        verify { userProgressRepo.save(any<UserProgressEntity>()) }
        verify(exactly = 0) {
            userAchievementRepo.save(match { it.unlocked })
        }
    }

    @Test
    fun `getUserAchievements should return correct progress for water achievements`() {
        val progress = UserProgressEntity(userId = userId, waterLiters = 30f)
        every { userProgressRepo.findById(userId) } returns Optional.of(progress)
        every { userAchievementRepo.findAllByUserId(userId) } returns emptyList()

        val result = service.getUserAchievements(userId)

        val waterAchievement = result.find { it.id == "water_24_liters" }
        assertNotNull(waterAchievement)
        assertEquals(30, waterAchievement!!.progress)
        assertFalse(waterAchievement.unlocked)
    }
}
