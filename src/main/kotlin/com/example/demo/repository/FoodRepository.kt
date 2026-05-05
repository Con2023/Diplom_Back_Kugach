package com.example.demo.repository

import com.example.demo.repository.entity.FoodEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FoodRepository : JpaRepository<FoodEntity, UUID> {
    fun findAllByUserIdOrderByDateAscTimeAsc(userId: UUID): List<FoodEntity>

    fun findByIdAndUserId(
        id: UUID,
        userId: UUID,
    ): FoodEntity?
}
