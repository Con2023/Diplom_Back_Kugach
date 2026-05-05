package com.example.demo.configs.security

import com.example.demo.configs.jwts.JwtAuthenticationFilter
import com.example.demo.configs.jwts.JwtUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.servlet.HandlerExceptionResolver

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtTokenProvider: JwtUtils,
    private val handlerExceptionResolver: HandlerExceptionResolver,
) {
    @Bean
    fun jwtAuthenticationFilter(): JwtAuthenticationFilter = JwtAuthenticationFilter(jwtTokenProvider, handlerExceptionResolver)

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.disable() }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        "/api/auth/register",
                        "/api/auth/login",
                        "/api/auth/refresh",
                        "/api/auth/forgot-password",
                        "/api/auth/reset-password",
                        "/error",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/api/files/**",
                    ).permitAll()
                    .requestMatchers("/api/auth/logout", "/api/auth/logout-all")
                    .authenticated()
                    .anyRequest()
                    .authenticated()
            }.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
            .headers { headers ->
                headers.httpStrictTransportSecurity { hsts ->
                    hsts
                        .includeSubDomains(true)
                        .preload(true)
                        .maxAgeInSeconds(31536000)
                }
                headers.contentTypeOptions { }
                headers.frameOptions { it.sameOrigin() }
            }

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
