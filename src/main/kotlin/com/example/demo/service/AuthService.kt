package com.example.demo.service

import com.example.demo.configs.jwts.JwtUtils
import com.example.demo.configs.tokens.TokenHasher
import com.example.demo.dto.mapper.toResponse
import com.example.demo.exception.DictionaryNotExist
import com.example.demo.exception.InvalidTokenException
import com.example.demo.model.AuthResult
import com.example.demo.model.LoginUser
import com.example.demo.model.RegisterUser
import com.example.demo.repository.PasswordResetTokenRepository
import com.example.demo.repository.TokenRepository
import com.example.demo.repository.UserRepository
import com.example.demo.repository.entity.PasswordResetTokenEntity
import com.example.demo.repository.mapper.toDomain
import com.example.demo.repository.mapper.toRefreshTokenEntity
import com.example.demo.repository.mapper.toUserEntity
import com.example.demo.utils.data.RequestUtils
import jakarta.transaction.Transactional
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime
import java.util.UUID
import kotlin.random.Random

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtils: JwtUtils,
    private val emailService: EmailService,
    private val tokenHasher: TokenHasher,
    private val passwordResetTokenRepository: PasswordResetTokenRepository,
) {
    fun register(command: RegisterUser): AuthResult {
        if (userRepository.findByEmail(command.email) != null) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use")
        }

        val savedUser = userRepository.save(command.toUserEntity(passwordEncoder))

        val accessToken = jwtUtils.generateAccessToken(savedUser.id!!, savedUser.email, listOf(savedUser.role!!))
        val rawRefreshToken = jwtUtils.generateRefreshToken(savedUser.id!!)

        val device = RequestUtils.getCurrentRequest()?.let { RequestUtils.getUserAgent(it) }
        val ip = RequestUtils.getCurrentRequest()?.let { RequestUtils.getClientIp(it) }
        tokenRepository.save(savedUser.toRefreshTokenEntity(rawRefreshToken, tokenHasher, device, ip))

        val userResponse = savedUser.toDomain().toResponse()
        return AuthResult(accessToken, rawRefreshToken, userResponse)
    }

    fun login(command: LoginUser): AuthResult {
        val user = userRepository.findByEmail(command.email) ?: throw Exception("Invalid credentials")
        if (!passwordEncoder.matches(command.password, user.password)) {
            throw BadCredentialsException("Invalid credentials")
        }
        if (!user.enabled) {
            throw DisabledException("User account is disabled")
        }
        val accessToken = jwtUtils.generateAccessToken(user.id!!, user.email, listOf(user.role!!))
        val rawRefreshToken = jwtUtils.generateRefreshToken(user.id!!)

        val device = RequestUtils.getCurrentRequest()?.let { RequestUtils.getUserAgent(it) }
        val ip = RequestUtils.getCurrentRequest()?.let { RequestUtils.getClientIp(it) }
        tokenRepository.save(user.toRefreshTokenEntity(rawRefreshToken, tokenHasher, device, ip))

        val userResponse = user.toDomain().toResponse()
        return AuthResult(accessToken, rawRefreshToken, userResponse)
    }

    fun refreshToken(rawToken: String): AuthResult {
        val claims = jwtUtils.parseClaims(rawToken)
        if (claims["type"] != "refresh") throw InvalidTokenException("Not a refresh token")

        val tokenHash = tokenHasher.hash(rawToken)
        val storedToken =
            tokenRepository.findByTokenHash(tokenHash)
                ?: throw InvalidTokenException("Refresh token not found")

        if (storedToken.expiresAt?.isBefore(LocalDateTime.now()) == true) {
            tokenRepository.delete(storedToken)
            throw InvalidTokenException("Refresh token expired")
        }

        val user =
            userRepository.findById(storedToken.user.id!!).orElse(null)
                ?: throw DictionaryNotExist("User not found")
        if (!user.enabled) {
            tokenRepository.deleteByUserId(user.id!!)
            throw DisabledException("User is disabled")
        }

        tokenRepository.delete(storedToken)

        val newAccessToken = jwtUtils.generateAccessToken(user.id!!, user.email, listOf(user.role!!))
        val newRawRefreshToken = jwtUtils.generateRefreshToken(user.id!!)

        tokenRepository.save(user.toRefreshTokenEntity(newRawRefreshToken, tokenHasher))

        val userResponse = user.toDomain().toResponse()
        return AuthResult(newAccessToken, newRawRefreshToken, userResponse)
    }

    fun logout(
        token: String,
        userId: UUID,
    ) {
        val tokenHash = tokenHasher.hash(token)
        val storedToken =
            tokenRepository.findByTokenHash(tokenHash)
                ?: throw InvalidTokenException("Refresh token not found")

        if (storedToken.user.id != userId) {
            throw AccessDeniedException("Token does not belong to current user")
        }
        tokenRepository.delete(storedToken)
    }

    fun revokeAllUserTokens(userId: UUID) {
        tokenRepository.deleteByUserId(userId)
    }

    @Transactional
    fun initiatePasswordReset(email: String) {
        val user = userRepository.findByEmail(email) ?: return

        val existingToken =
            passwordResetTokenRepository
                .findFirstByUserIdAndCreatedAtAfter(user.id!!, LocalDateTime.now().minusMinutes(2))
        if (existingToken.isNotEmpty()) {
            log.info("Password reset already requested recently")
            return
        }

        val code = Random.nextInt(100000, 1000000).toString()
        val codeHash = tokenHasher.hash(code)

        val tokenEntity =
            PasswordResetTokenEntity(
                tokenHash = codeHash,
                user = user,
                createdAt = LocalDateTime.now(),
            )

        try {
            passwordResetTokenRepository.save(tokenEntity)
            emailService.sendPasswordResetCode(user.email, code)
        } catch (e: Exception) {
            passwordResetTokenRepository.delete(tokenEntity)
            throw ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Unable to send reset email")
        }
    }

    fun resetPassword(
        code: String,
        newPassword: String,
    ) {
        val tokenHash = tokenHasher.hash(code)

        val tokenEntity =
            passwordResetTokenRepository.findByTokenHash(tokenHash)
                ?: throw InvalidTokenException("Invalid or expired token")

        if (tokenEntity.expiresAt.isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(tokenEntity)
            throw InvalidTokenException("Token expired")
        }
        val user = tokenEntity.user

        tokenRepository.deleteByUserId(user.id!!)

        user.password = passwordEncoder.encode(newPassword)
        userRepository.save(user)

        passwordResetTokenRepository.delete(tokenEntity)
    }
}
