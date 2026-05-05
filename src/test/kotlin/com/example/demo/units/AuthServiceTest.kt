package com.example.demo.units

import com.example.demo.configs.jwts.JwtUtils
import com.example.demo.configs.tokens.TokenHasher
import com.example.demo.exception.InvalidTokenException
import com.example.demo.model.LoginUser
import com.example.demo.model.RegisterUser
import com.example.demo.repository.PasswordResetTokenRepository
import com.example.demo.repository.TokenRepository
import com.example.demo.repository.UserRepository
import com.example.demo.repository.entity.PasswordResetTokenEntity
import com.example.demo.repository.entity.RefreshTokensEntity
import com.example.demo.repository.entity.UserEntity
import com.example.demo.service.AuthService
import com.example.demo.service.EmailService
import com.example.demo.utils.Role
import io.jsonwebtoken.Claims
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.assertNotNull

class AuthServiceTest {
    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val tokenRepository = mockk<TokenRepository>(relaxed = true)
    private val passwordEncoder = mockk<PasswordEncoder>(relaxed = true)
    private val jwtUtils = mockk<JwtUtils>(relaxed = true)
    private val emailService = mockk<EmailService>(relaxed = true)
    private val tokenHasher = mockk<TokenHasher>(relaxed = true)
    private val passwordResetTokenRepository = mockk<PasswordResetTokenRepository>(relaxed = true)

    private val authService =
        AuthService(
            userRepository,
            tokenRepository,
            passwordEncoder,
            jwtUtils,
            emailService,
            tokenHasher,
            passwordResetTokenRepository,
        )

    @Test
    fun `register should throw if email already exists`() {
        val command = RegisterUser("John", "Doe", "john@example.com", "password")
        every { userRepository.findByEmail(command.email) } returns mockk()

        assertThrows<ResponseStatusException> {
            authService.register(command)
        }
    }

    @Test
    fun `register should succeed and return tokens`() {
        val command = RegisterUser("John", "Doe", "john@example.com", "password")
        val userEntity =
            UserEntity(id = UUID.randomUUID(), email = command.email, role = Role.ROLE_USER, enabled = true, password = "password")
        every { userRepository.findByEmail(command.email) } returns null
        every { userRepository.save(any()) } returns userEntity
        every { jwtUtils.generateAccessToken(any(), any(), any()) } returns "accessToken"
        every { jwtUtils.generateRefreshToken(any()) } returns "refreshToken"
        every { tokenRepository.save(any()) } answers { firstArg() }

        val result = authService.register(command)
        assertNotNull(result.accessToken)
        assertNotNull(result.refreshToken)
        verify { tokenRepository.save(any()) }
    }

    @Test
    fun `login should throw on bad credentials`() {
        val command = LoginUser("john@example.com", "wrong")
        val user = mockk<UserEntity>(relaxed = true)
        every { userRepository.findByEmail(command.email) } returns user
        every { user.enabled } returns true
        every { passwordEncoder.matches(command.password, user.password) } returns false

        assertThrows<BadCredentialsException> {
            authService.login(command)
        }
    }

    @Test
    fun `login should throw on disabled user`() {
        val command = LoginUser("john@example.com", "password")
        val user = UserEntity(id = UUID.randomUUID(), email = command.email, password = "hashed", enabled = false)
        every { userRepository.findByEmail(command.email) } returns user
        every { passwordEncoder.matches(command.password, user.password) } returns true

        assertThrows<DisabledException> {
            authService.login(command)
        }
    }

    @Test
    fun `login should return tokens on success`() {
        val command = LoginUser("john@example.com", "password")
        val user = UserEntity(id = UUID.randomUUID(), email = command.email, password = "password", role = Role.ROLE_USER, enabled = true)
        every { userRepository.findByEmail(command.email) } returns user
        every { passwordEncoder.matches(command.password, user.password) } returns true
        every { jwtUtils.generateAccessToken(any(), any(), any()) } returns "accessToken"
        every { jwtUtils.generateRefreshToken(any()) } returns "refreshToken"
        every { tokenRepository.save(any()) } answers { firstArg() }

        val result = authService.login(command)
        assertNotNull(result.accessToken)
        verify { tokenRepository.save(any()) }
    }

    @Test
    fun `refreshToken should throw if token is not refresh type`() {
        val rawToken = "refreshToken"
        val claims = mockk<Claims>()
        every { claims["type"] } returns "access" // если jwtUtils.parseClaims читает claim "type"
        every { jwtUtils.parseClaims(rawToken) } returns claims

        assertThrows<InvalidTokenException> {
            authService.refreshToken(rawToken)
        }
    }

    @Test
    fun `logout should delete token`() {
        val rawToken = "refreshToken"
        val userId = UUID.randomUUID()
        val tokenHash = "hash"
        val storedToken = mockk<RefreshTokensEntity>(relaxed = true)
        every { tokenHasher.hash(rawToken) } returns tokenHash
        every { tokenRepository.findByTokenHash(tokenHash) } returns storedToken
        every { storedToken.user.id } returns userId

        authService.logout(rawToken, userId)
        verify { tokenRepository.delete(storedToken) }
    }

    @Test
    fun `resetPassword should update password and delete tokens`() {
        val code = "123456"
        val newPassword = "newPassword"
        val tokenHash = "hash"
        val user = UserEntity(id = UUID.randomUUID(), email = "test@example.com", password = "old")
        val tokenEntity =
            PasswordResetTokenEntity(
                id = UUID.randomUUID(),
                tokenHash = tokenHash,
                user = user,
                createdAt = LocalDateTime.now(),
                expiresAt = LocalDateTime.now().plusMinutes(15),
            )
        every { tokenHasher.hash(code) } returns tokenHash
        every { passwordResetTokenRepository.findByTokenHash(tokenHash) } returns tokenEntity
        every { userRepository.save(any()) } answers { firstArg() }
        every { tokenRepository.deleteByUserId(any()) } returns Unit
        every { passwordResetTokenRepository.delete(any()) } returns Unit

        authService.resetPassword(code, newPassword)

        verify { userRepository.save(match { it.password != "old" }) }
        verify { tokenRepository.deleteByUserId(user.id!!) }
        verify { passwordResetTokenRepository.delete(tokenEntity) }
    }
}
