package com.example.demo.repository.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "food_entries")
data class FoodEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    @Column(name = "user_id", nullable = false)
    val userId: UUID,
    @Column(nullable = false)
    var name: String,
    @Column(nullable = false)
    var calories: Int,
    @Column(nullable = false)
    var protein: Int,
    @Column(nullable = false)
    var fats: Int,
    @Column(nullable = false)
    var carbs: Int,
    @Column(name = "meal_type", nullable = false)
    var mealType: String,
    @Column(nullable = false)
    var time: String,
    @Column(nullable = false)
    var date: String,
)
