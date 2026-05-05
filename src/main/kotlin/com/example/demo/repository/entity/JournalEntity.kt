package com.example.demo.repository.entity

import com.example.demo.utils.TagListConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "journal_entries")
data class JournalEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    @Column(name = "user_id", nullable = false)
    val userId: UUID,
    @Column(nullable = false)
    var type: String,
    @Column(nullable = false)
    var date: String,
    @Column(nullable = false)
    var title: String,
    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String,
    @Column(nullable = false)
    var mood: String,
    @Column(name = "tags", columnDefinition = "TEXT")
    @Convert(converter = TagListConverter::class)
    var tags: List<String> = emptyList(),
)
