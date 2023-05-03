package com.example.bgm.jwt

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


class JwtUser(
    @get:JsonIgnore val id: Long?,
    private val nickname: String,
    private val password: String,
    private val authorities: Collection<GrantedAuthority?>
): UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority?> {
        return authorities
    }

    @JsonIgnore
    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return nickname
    }

    @JsonIgnore
    override fun isAccountNonExpired(): Boolean {
        return true
    }

    @JsonIgnore
    override fun isAccountNonLocked(): Boolean {
        return true
    }

    @JsonIgnore
    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}
