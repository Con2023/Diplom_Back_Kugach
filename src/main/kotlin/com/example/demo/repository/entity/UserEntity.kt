package com.example.demo.repository.entity

import com.example.demo.utils.Role
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "users", schema = "data")
data class UserEntity(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID? = null,
    @Column(unique = true, nullable = false)
    var email: String,
    @Column(name = "first_name")
    var firstName: String? = null,
    @Column(name = "second_name")
    var secondName: String? = null,
    @Column(nullable = false)
    var password: String,
    @Enumerated(EnumType.STRING)
    var role: Role? = Role.ROLE_USER,
    var gender: String? = null,
    @Column(nullable = false)
    var enabled: Boolean = true,
    var age: Int? = null,
    var weight: Double? = null,
    @Column(name = "target_weight")
    var targetWeight: Double? = null,
    var height: Double? = null,
    var image: String? = null,
    @Column(name = "birth_date")
    var birthDate: LocalDate? = null,
    @Column(name = "created_at")
    var createdAt: LocalDateTime? = null,
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
)
