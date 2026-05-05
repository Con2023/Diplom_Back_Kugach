package com.example.demo.controller

import com.example.demo.configs.AuthenticatedUser
import com.example.demo.dto.response.AchievementResponse
import com.example.demo.service.AchievementService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@Tag(name = "Достижения", description = "Управление достижениями")
@RequestMapping("/api/achievements")
class AchievementController(
    private val achievementService: AchievementService,
) {
    @GetMapping("/me")
    fun getMyAchievements(
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
    ): List<AchievementResponse> = achievementService.getUserAchievements(authenticatedUser.id)
}
