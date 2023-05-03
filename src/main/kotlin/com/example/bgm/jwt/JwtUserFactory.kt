package com.example.bgm.jwt

import com.example.bgm.entities.Person
import com.example.bgm.entities.Role
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.function.Function
import java.util.stream.Collectors


object JwtUserFactory {
    fun create(person: Person): JwtUser {
        return JwtUser(
            person.id,
            person.nickname,
            person.password,
            mapToGrantedAuthorities(person.roles)
        )
    }

    private fun mapToGrantedAuthorities(userRoles: List<Role>): List<GrantedAuthority> {
        return userRoles.stream()
            .map { role: Role ->
                SimpleGrantedAuthority(
                    role.name
                )
            }.collect(Collectors.toList<GrantedAuthority>())
    }
}
