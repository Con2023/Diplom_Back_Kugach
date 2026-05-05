package com.example.demo.configs.jwts

import com.example.demo.configs.AuthenticatedUser
import com.example.demo.configs.tokens.TokenValidationResult
import com.example.demo.exception.InvalidTokenException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerExceptionResolver

class JwtAuthenticationFilter(
    private val jwtUtils: JwtUtils,
    private val handlerExceptionResolver: HandlerExceptionResolver,
) : OncePerRequestFilter() {
    private val publicPaths =
        listOf(
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/refresh",
            "/api/auth/forgot-password",
            "/api/auth/reset-password",
            "/error",
            "/swagger-ui",
            "/v3/api-docs",
        )

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        return publicPaths.any { path.startsWith(it) }
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            val token = extractToken(request)
            if (token == null) {
                filterChain.doFilter(request, response)
                return
            }

            when (val result = jwtUtils.validateToken(token)) {
                is TokenValidationResult.Valid -> {
                    val claims = result.claims
                    if (claims["type"] != "access") {
                        throw InvalidTokenException("Not an access token")
                    }
                    println("TOKEN: $token")
                    println("TYPE: ${claims["type"]}")
                    val userId = jwtUtils.getUserIdFromClaims(claims)
                    val email = jwtUtils.getEmailFromClaims(claims)
                    val roles = jwtUtils.getRolesFromClaims(claims)

                    val authenticatedUser = AuthenticatedUser(userId, email, roles)
                    val authentication =
                        UsernamePasswordAuthenticationToken(
                            authenticatedUser,
                            null,
                            authenticatedUser.getAuthorities(),
                        )
                    SecurityContextHolder.getContext().authentication = authentication
                }

                is TokenValidationResult.Expired -> {
                    throw InvalidTokenException("Token expired")
                }

                is TokenValidationResult.Invalid -> {
                    throw InvalidTokenException("Invalid token: ${result.reason}")
                }
            }
            filterChain.doFilter(request, response)
        } catch (ex: InvalidTokenException) {
            handlerExceptionResolver.resolveException(request, response, null, ex)
            return
        }
    }

    private fun extractToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (!bearerToken.isNullOrEmpty() && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else {
            null
        }
    }
}
