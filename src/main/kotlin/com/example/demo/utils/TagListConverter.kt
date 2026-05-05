package com.example.demo.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class TagListConverter : AttributeConverter<List<String>, String> {
    private val mapper = ObjectMapper()

    override fun convertToDatabaseColumn(attribute: List<String>?): String? {
        if (attribute.isNullOrEmpty()) return null
        return mapper.writeValueAsString(attribute)
    }

    override fun convertToEntityAttribute(dbData: String?): List<String> {
        if (dbData.isNullOrBlank()) return emptyList()
        return try {
            mapper.readValue(dbData)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
