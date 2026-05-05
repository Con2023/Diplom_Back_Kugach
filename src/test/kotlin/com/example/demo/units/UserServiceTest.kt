package com.example.demo.units

import com.example.demo.model.UpdateUserRequest
import com.example.demo.model.User
import com.example.demo.repository.UserRepository
import com.example.demo.repository.entity.UserEntity
import com.example.demo.service.AvatarStorageService
import com.example.demo.service.UserService
import com.example.demo.utils.Role
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.util.Optional
import java.util.UUID
import kotlin.test.assertTrue

class UserServiceTest {
    private lateinit var userRepository: UserRepository
    private lateinit var avatarStorageService: AvatarStorageService
    private lateinit var userService: UserService

    private val userId = UUID.randomUUID()
    private val userEntity =
        UserEntity(
            id = userId,
            email = "test@example.com",
            password = "pass",
            firstName = "John",
            secondName = "Doe",
            age = 30,
            gender = "male",
            weight = 80.0,
            height = 180.0,
            targetWeight = 75.0,
            image = null,
            role = Role.ROLE_USER,
            enabled = true,
        )

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        avatarStorageService = mockk()
        userService = UserService(userRepository, avatarStorageService)
    }

    @Test
    fun `getUserById should return user when exists`() {
        every { userRepository.findById(userId) } returns Optional.of(userEntity)

        val result: User = userService.getUserById(userId)

        assertEquals(userId, result.id)
        assertEquals("test@example.com", result.email)
        assertEquals("John", result.firstName)
        verify(exactly = 1) { userRepository.findById(userId) }
    }

    @Test
    fun `getUserById should throw ResponseStatusException when user not found`() {
        every { userRepository.findById(userId) } returns Optional.empty()

        val exception =
            assertThrows<ResponseStatusException> {
                userService.getUserById(userId)
            }
        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertEquals("User not found", exception.reason)
        verify(exactly = 1) { userRepository.findById(userId) }
    }

    @Test
    fun `updateUser should update all fields and return updated user`() {
        val request =
            UpdateUserRequest(
                firstName = "Jane",
                secondName = "Smith",
                age = 28,
                gender = "female",
                weight = 65.0,
                height = 170.0,
                targetWeight = 60.0,
            )

        every { userRepository.findById(userId) } returns Optional.of(userEntity)
        every { userRepository.save(userEntity) } returns userEntity

        val result = userService.updateUser(userId, request)

        assertEquals(request.firstName, userEntity.firstName)
        assertEquals(request.secondName, userEntity.secondName)
        assertEquals(request.age, userEntity.age)
        assertEquals(request.gender, userEntity.gender)
        assertEquals(request.weight, userEntity.weight)
        assertEquals(request.height, userEntity.height)
        assertEquals(request.targetWeight, userEntity.targetWeight)

        assertEquals(userId, result.id)
        verify { userRepository.save(userEntity) }
    }

    @Test
    fun `updateUser should update only provided fields`() {
        val request =
            UpdateUserRequest(
                firstName = "Jane",
                secondName = null,
                age = null,
                gender = null,
                weight = null,
                height = null,
                targetWeight = null,
            )
        val originalLastName = userEntity.secondName
        val originalAge = userEntity.age

        every { userRepository.findById(userId) } returns Optional.of(userEntity)
        every { userRepository.save(userEntity) } returns userEntity

        userService.updateUser(userId, request)

        assertEquals("Jane", userEntity.firstName)
        assertEquals(originalLastName, userEntity.secondName)
        assertEquals(originalAge, userEntity.age)
        verify { userRepository.save(userEntity) }
    }

    @Test
    fun `updateUser should throw ResponseStatusException when user not found`() {
        val request =
            UpdateUserRequest(
                firstName = "Jane",
                age = 10,
                weight = 100.0,
                height = 120.0,
                gender = "w",
                secondName = "",
                targetWeight = 100.0,
            )
        every { userRepository.findById(userId) } returns Optional.empty()

        val exception =
            assertThrows<ResponseStatusException> {
                userService.updateUser(userId, request)
            }
        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        verify(exactly = 0) { userRepository.save(any()) }
    }

    private fun createMockFile(
        contentType: String = "image/jpeg",
        size: Long = 1024,
        isEmpty: Boolean = false,
    ): MultipartFile {
        val mock = mockk<MultipartFile>()
        every { mock.contentType } returns contentType
        every { mock.size } returns size
        every { mock.isEmpty } returns isEmpty
        every { mock.originalFilename } returns "avatar.jpg"
        every { mock.inputStream } returns mockk()
        return mock
    }

    @Test
    fun `uploadAvatar should save new avatar when user has no existing image`() {
        val file = createMockFile()
        val newUrl = "https://storage.example.com/avatars/$userId/avatar.jpg"

        every { userRepository.findById(userId) } returns Optional.of(userEntity)
        every { avatarStorageService.save(file, userId) } returns newUrl
        every { userRepository.save(userEntity) } returns userEntity

        val result = userService.uploadAvatar(userId, file)

        assertEquals(newUrl, result)
        assertEquals(newUrl, userEntity.image)
        verify(exactly = 0) { avatarStorageService.delete(any()) }
        verify { avatarStorageService.save(file, userId) }
        verify { userRepository.save(userEntity) }
    }

    @Test
    fun `uploadAvatar should delete old avatar before saving new one`() {
        val userWithImage = userEntity.copy(image = "old/url.jpg")
        val file = createMockFile()
        val newUrl = "https://storage.example.com/avatars/$userId/new.jpg"

        every { userRepository.findById(userId) } returns Optional.of(userWithImage)
        every { avatarStorageService.delete("old/url.jpg") } returns Unit
        every { avatarStorageService.save(file, userId) } returns newUrl
        every { userRepository.save(userWithImage) } returns userWithImage

        val result = userService.uploadAvatar(userId, file)

        assertEquals(newUrl, result)
        verify { avatarStorageService.delete("old/url.jpg") }
        verify { avatarStorageService.save(file, userId) }
    }

    @Test
    fun `uploadAvatar should throw BAD_REQUEST when file is empty`() {
        val file = createMockFile(isEmpty = true)

        every { userRepository.findById(userId) } returns Optional.of(userEntity)

        val exception =
            assertThrows<ResponseStatusException> {
                userService.uploadAvatar(userId, file)
            }
        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
        assertEquals("File is empty", exception.reason)
        verify(exactly = 0) { avatarStorageService.save(any(), any()) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `uploadAvatar should throw BAD_REQUEST when content type is not allowed`() {
        val invalidTypes = listOf("image/gif", "text/plain", "application/octet-stream")
        invalidTypes.forEach { contentType ->
            val file = createMockFile(contentType = contentType)

            every { userRepository.findById(userId) } returns Optional.of(userEntity)

            val exception =
                assertThrows<ResponseStatusException> {
                    userService.uploadAvatar(userId, file)
                }
            assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
            assertTrue(exception.reason?.contains("Only JPEG, PNG or WEBP") == true)
            verify(exactly = 0) { avatarStorageService.save(any(), any()) }
            clearMocks(userRepository, avatarStorageService)
        }
    }

    @Test
    fun `uploadAvatar should throw BAD_REQUEST when file size exceeds 5MB`() {
        val file = createMockFile(size = 6 * 1024 * 1024) // 6 MB

        every { userRepository.findById(userId) } returns Optional.of(userEntity)

        val exception =
            assertThrows<ResponseStatusException> {
                userService.uploadAvatar(userId, file)
            }
        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
        assertEquals("File too large (max 5MB)", exception.reason)
        verify(exactly = 0) { avatarStorageService.save(any(), any()) }
    }

    @Test
    fun `uploadAvatar should throw NOT_FOUND when user does not exist`() {
        val file = createMockFile()
        every { userRepository.findById(userId) } returns Optional.empty()

        val exception =
            assertThrows<ResponseStatusException> {
                userService.uploadAvatar(userId, file)
            }
        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertEquals("User not found", exception.reason)
        verify(exactly = 0) { avatarStorageService.save(any(), any()) }
    }
}
