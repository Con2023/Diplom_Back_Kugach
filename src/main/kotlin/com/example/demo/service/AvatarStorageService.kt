package com.example.demo.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.UUID

@Service
class AvatarStorageService(
    @Value("\${app.avatar.storage.path}") private val storagePath: String,
    @Value("\${app.avatar.base-url}") private val baseUrl: String,
) {
    fun save(
        file: MultipartFile,
        userId: UUID,
    ): String {
        val directory = Paths.get(storagePath)
        if (!Files.exists(directory)) Files.createDirectories(directory)

        val extension = file.originalFilename?.substringAfterLast('.') ?: "jpg"
        val fileName = "$userId-${System.currentTimeMillis()}.$extension"
        val targetPath = directory.resolve(fileName)

        file.inputStream.use { input ->
            Files.copy(input, targetPath, StandardCopyOption.REPLACE_EXISTING)
        }

        return "$baseUrl/avatars/$fileName"
    }

    fun delete(url: String) {
        val fileName = url.substringAfterLast('/')
        val filePath = Paths.get(storagePath, fileName)
        Files.deleteIfExists(filePath)
    }
}
