package com.example.demo.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.io.File
import java.nio.file.Paths

@RestController
@RequestMapping("/api/files/avatars")
class AvatarFileController(
    @Value("\${app.avatar.storage.path}") private val storagePath: String,
) {
    @GetMapping("/{filename}")
    fun serveAvatar(
        @PathVariable filename: String,
    ): ResponseEntity<Resource> {
        val file = Paths.get(storagePath).resolve(filename).toFile()
        if (!file.exists()) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Avatar not found")
        }
        val resource = FileSystemResource(file)
        val contentType = determineContentType(file)
        return ResponseEntity
            .ok()
            .contentType(MediaType.parseMediaType(contentType))
            .body(resource)
    }

    private fun determineContentType(file: File): String {
        val extension = file.extension.lowercase()
        return when (extension) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "webp" -> "image/webp"
            else -> "application/octet-stream"
        }
    }
}
