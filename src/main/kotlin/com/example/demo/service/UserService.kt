package com.example.demo.service

import com.example.demo.model.UpdateUserRequest
import com.example.demo.model.User
import com.example.demo.repository.UserRepository
import com.example.demo.repository.mapper.toDomain
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val avatarStorageService: AvatarStorageService,
) {
    fun getUserById(userId: UUID): User {
        val userEntity =
            userRepository
                .findById(userId)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "User not found") }
        return userEntity.toDomain()
    }

    fun updateUser(
        userId: UUID,
        request: UpdateUserRequest,
    ): User {
        val userEntity =
            userRepository
                .findById(userId)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "User not found") }

        request.firstName?.let { userEntity.firstName = it }
        request.secondName?.let { userEntity.secondName = it }
        request.age?.let { userEntity.age = it }
        request.gender?.let { userEntity.gender = it }
        request.weight?.let { userEntity.weight = it }
        request.height?.let { userEntity.height = it }
        request.targetWeight?.let { userEntity.targetWeight = it }

        val updatedEntity = userRepository.save(userEntity)
        return updatedEntity.toDomain()
    }

    fun uploadAvatar(
        userId: UUID,
        file: MultipartFile,
    ): String {
        val userEntity =
            userRepository
                .findById(userId)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "User not found") }

        if (file.isEmpty) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty")
        }
        val allowedTypes = listOf("image/jpeg", "image/png", "image/webp")
        if (file.contentType !in allowedTypes) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Only JPEG, PNG or WEBP images are allowed")
        }
        if (file.size > 5 * 1024 * 1024) { // 5 MB
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "File too large (max 5MB)")
        }

        userEntity.image?.let { oldUrl ->
            avatarStorageService.delete(oldUrl)
        }

        val newAvatarUrl = avatarStorageService.save(file, userId)
        userEntity.image = newAvatarUrl
        userRepository.save(userEntity)

        return newAvatarUrl
    }
}
