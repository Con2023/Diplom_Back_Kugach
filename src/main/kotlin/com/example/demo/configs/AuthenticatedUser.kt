package com.example.demo.configs

import com.example.demo.utils.Role
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.UUID

data class AuthenticatedUser(
    val id: UUID,
    val email: String,
    val roles: Set<Role>,
) {
    fun getAuthorities(): Collection<GrantedAuthority> = roles.map { SimpleGrantedAuthority(it.name) }
}
