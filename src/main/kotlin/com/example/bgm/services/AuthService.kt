package com.example.bgm.services

import com.example.bgm.entities.dto.AuthenticationRequestEntity
import com.example.bgm.entities.dto.AuthenticationResponseEntity
import com.example.bgm.entities.dto.CreatePersonRequestEntity
import com.example.bgm.entities.Person
import com.example.bgm.entities.Role
import com.example.bgm.entities.jwt.Token
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.jwt.JwtTokenProvider
import com.example.bgm.repositories.PersonRepo
import com.example.bgm.repositories.RoleRepo
import com.example.bgm.repositories.jwt.TokenRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties.Http
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class AuthService {

    @Autowired private lateinit var passwordEncoder: BCryptPasswordEncoder
    @Autowired private lateinit var authenticationManager: AuthenticationManager
    @Autowired private lateinit var jwtTokenProvider: JwtTokenProvider
    @Autowired private lateinit var personRepo: PersonRepo
    @Autowired private lateinit var tokenRepo: TokenRepo
    @Autowired private lateinit var roleRepo: RoleRepo

    @Transactional
    fun createPerson(createPersonRequest: CreatePersonRequestEntity): ResponseEntity<*> {
        if (createPersonRequest.age != null) {
            if (createPersonRequest.age < 0 || createPersonRequest.age > 200) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Возраст может быть от 0 до 200")
            }
        }
        return if(!personRepo.existsByNickname(createPersonRequest.nickname)) {
            val person = Person(createPersonRequest.name,
                    createPersonRequest.nickname,
                    passwordEncoder.encode(createPersonRequest.password),
                    createPersonRequest.secretWord,
                    createPersonRequest.gender,
                    createPersonRequest.city)
            person.roles.add(roleRepo.findByName("ROLE_USER"))
            person.age = createPersonRequest.age
            personRepo.save(person)
            ResponseEntity.ok("done")
        } else {
            ResponseEntity.status(HttpStatus.CONFLICT).body("Такой никнейм уже занят")
        }
    }

    @Transactional
    fun login(authenticationRequest: AuthenticationRequestEntity): ResponseEntity<*> {
        return try {
            val nickname = authenticationRequest.nickname
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(nickname, authenticationRequest.password))
            val person: Person = personRepo.findByNickname(nickname)
                ?: throw UsernameNotFoundException("Person with nickname: $nickname not found")
            val token = jwtTokenProvider.createToken(nickname, person.roles)
            tokenRepo.deleteAllByPerson(person)
            tokenRepo.save(Token(token, person))
            ResponseEntity.ok(AuthenticationResponseEntity(nickname, token, person.getStringRole()))
        } catch (e: AuthenticationException) {
            print(e.message)
            ResponseEntity.status(HttpStatus.CONFLICT).body("Неверный никнейм или пароль")
        }
    }

    fun logout(authPerson: JwtPerson) {
        val person = personRepo.findByNickname(authPerson.username)
            ?: throw UsernameNotFoundException("Person with nickname: ${authPerson.username} not found")
        jwtTokenProvider.invalidateTokensOfUser(person)
    }

}