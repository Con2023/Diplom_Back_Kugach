package com.example.demo.controller

import com.example.demo.configs.AuthenticatedUser
import com.example.demo.dto.response.StatisticsResponse
import com.example.demo.service.StatisticsService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Статистика")
@RequestMapping("/api/statistics")
class StatisticsController(
    private val statisticsService: StatisticsService,
) {
    @GetMapping("/me")
    fun getMyStatistics(
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
    ): StatisticsResponse = statisticsService.getStatistics(authenticatedUser.id)
}
