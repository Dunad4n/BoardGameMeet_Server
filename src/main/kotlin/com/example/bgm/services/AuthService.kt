package com.example.bgm.services

import com.example.bgm.controller.dto.AuthenticationRequestEntity
import com.example.bgm.controller.dto.AuthenticationResponseEntity
import com.example.bgm.controller.dto.CreatePersonRequestEntity
import com.example.bgm.entities.Person
import com.example.bgm.entities.Role
import com.example.bgm.entities.jwt.Token
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.jwt.JwtTokenProvider
import com.example.bgm.repositories.PersonRepo
import com.example.bgm.repositories.jwt.TokenRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService {

    @Autowired
    lateinit var passwordEncoder: BCryptPasswordEncoder

    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    @Autowired
    lateinit var jwtTokenProvider: JwtTokenProvider

    @Autowired
    lateinit var personRepo: PersonRepo

    @Autowired
    lateinit var tokenRepo: TokenRepo

    fun createPerson(createPersonRequest: CreatePersonRequestEntity, role: Role) {
        if(!personRepo.existsByNickname(createPersonRequest.nickname)) {
            personRepo.save(
                Person(
                    createPersonRequest.name,
                    createPersonRequest.nickname,
                    passwordEncoder.encode(createPersonRequest.password),
                    createPersonRequest.secretWord,
                    createPersonRequest.gender,
                    createPersonRequest.city
                )
            )
        } else {
            throw Exception("this nickname is occupied")
        }
    }

    fun login(authenticationRequest: AuthenticationRequestEntity): AuthenticationResponseEntity {
        return try {
            val nickname = authenticationRequest.nickname
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(nickname, authenticationRequest.password))
            val person: Person = personRepo.findByNickname(nickname)
                ?: throw UsernameNotFoundException("Person with nickname: $nickname not found")
            val token = jwtTokenProvider.createToken(nickname, person.roles)
            tokenRepo.save(Token(token, person))
            AuthenticationResponseEntity(nickname, token, person.getStringRole())
        } catch (e: AuthenticationException) {
            throw BadCredentialsException("Invalid nickname or password")
        }
    }

    fun logout(authPerson: JwtPerson) {
        val person = personRepo.findByNickname(authPerson.username)
            ?: throw UsernameNotFoundException("Person with nickname: ${authPerson.username} not found")
        jwtTokenProvider.invalidateTokensOfUser(person)
    }

}