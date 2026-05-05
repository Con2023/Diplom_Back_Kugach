package com.example.demo.service

import com.example.demo.dto.mapper.toEntity
import com.example.demo.dto.mapper.toResponse
import com.example.demo.dto.request.FoodEntryRequest
import com.example.demo.dto.response.FoodEntryResponse
import com.example.demo.repository.FoodRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class FoodService(
    private val foodRepository: FoodRepository,
) {
    @Transactional
    fun createEntry(
        userId: UUID,
        request: FoodEntryRequest,
    ): FoodEntryResponse = foodRepository.save(request.toEntity(userId)).toResponse()

    @Transactional(readOnly = true)
    fun getUserEntries(userId: UUID): List<FoodEntryResponse> =
        foodRepository
            .findAllByUserIdOrderByDateAscTimeAsc(userId)
            .map { it.toResponse() }

    @Transactional
    fun deleteEntry(
        userId: UUID,
        entryId: UUID,
    ) {
        val entity =
            foodRepository.findByIdAndUserId(entryId, userId)
                ?: throw NoSuchElementException("Entry not found or access denied")

        foodRepository.delete(entity)
    }
}
