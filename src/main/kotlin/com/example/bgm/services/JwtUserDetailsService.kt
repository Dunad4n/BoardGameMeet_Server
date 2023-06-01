package com.example.bgm.services

import com.example.bgm.entities.Person
import com.example.bgm.jwt.JwtUserFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service


@Service
class JwtUserDetailsService: UserDetailsService {

    @Autowired
    private lateinit var personService: PersonService

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val person: Person = personService.getByNickname(username)
        return JwtUserFactory.create(person)
    }
}
