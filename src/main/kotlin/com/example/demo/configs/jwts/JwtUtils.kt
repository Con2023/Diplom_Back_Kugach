package com.example.demo.configs.jwts

import com.example.demo.configs.tokens.TokenValidationResult
import com.example.demo.utils.Role
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.IncorrectClaimException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.MissingClaimException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.UnsupportedJwtException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Duration
import java.util.Date
import java.util.UUID

@Component
class JwtUtils(
    private val privateKey: RSAPrivateKey,
    private val publicKey: RSAPublicKey,
    @Value("\${jwt.access-token-expiration}") private val accessTokenExpiration: Duration,
    @Value("\${jwt.refresh-token-expiration}") private val refreshTokenExpiration: Duration,
    @Value("\${jwt.issuer:my-app}") private val issuer: String,
    @Value("\${jwt.audience:my-app-client}") private val audience: String,
) {
    fun generateAccessToken(
        userId: UUID,
        email: String,
        authorities: Collection<Role>,
    ): String {
        val roles = authorities.map { it.name }
        return Jwts
            .builder()
            .setSubject(userId.toString())
            .claim("email", email)
            .claim("roles", roles)
            .claim("type", "access")
            .setIssuer(issuer)
            .setAudience(audience)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + accessTokenExpiration.toMillis()))
            .signWith(privateKey, SignatureAlgorithm.RS256)
            .compact()
    }

    fun generateRefreshToken(userId: UUID): String =
        Jwts
            .builder()
            .setSubject(userId.toString())
            .claim("type", "refresh")
            .setIssuer(issuer)
            .setAudience(audience)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + refreshTokenExpiration.toMillis()))
            .signWith(privateKey, SignatureAlgorithm.RS256)
            .compact()

    fun parseClaims(token: String): Claims =
        Jwts
            .parserBuilder()
            .setSigningKey(publicKey)
            .requireIssuer(issuer)
            .requireAudience(audience)
            .build()
            .parseClaimsJws(token)
            .body

    fun validateToken(token: String): TokenValidationResult =
        try {
            val claims = parseClaims(token)
            if (claims.expiration.before(Date())) {
                TokenValidationResult.Expired
            } else {
                TokenValidationResult.Valid(claims)
            }
        } catch (e: ExpiredJwtException) {
            TokenValidationResult.Expired
        } catch (e: MalformedJwtException) {
            TokenValidationResult.Invalid("Malformed token")
        } catch (e: UnsupportedJwtException) {
            TokenValidationResult.Invalid("Unsupported token")
        } catch (e: IllegalArgumentException) {
            TokenValidationResult.Invalid("Token is empty or null")
        } catch (e: MissingClaimException) {
            TokenValidationResult.Invalid("Missing required claim: ${e.claimName}")
        } catch (e: IncorrectClaimException) {
            TokenValidationResult.Invalid("Invalid claim: ${e.claimName}")
        } catch (e: JwtException) {
            TokenValidationResult.Invalid("Invalid signature or structure")
        }

    fun getRolesFromClaims(claims: Claims): Set<Role> {
        val roleNames = claims["roles"] as? List<String> ?: emptyList()
        return roleNames.mapNotNull { runCatching { Role.valueOf(it) }.getOrNull() }.toSet()
    }

    fun getUserIdFromClaims(claims: Claims): UUID = UUID.fromString(claims.subject)

    fun getEmailFromClaims(claims: Claims): String = claims["email"] as String
}
