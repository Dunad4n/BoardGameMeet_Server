package com.example.bgm.controller

import com.example.bgm.entities.Person
import com.example.bgm.jwt.JwtTokenProvider
import com.example.bgm.repositories.PersonRepo
import com.example.bgm.repositories.RoleRepo
import com.example.bgm.services.PersonService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthorizationController {

    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    @Autowired
    lateinit var jwtTokenProvider: JwtTokenProvider

    @Autowired
    lateinit var personService: PersonService

    @Autowired
    lateinit var personRepo: PersonRepo

    @Autowired
    lateinit var roleRepo: RoleRepo

    @Autowired
    lateinit var passwordEncoder: BCryptPasswordEncoder

    @PostMapping("login")
    fun login(@RequestBody authenticationRequest: AuthenticationRequestEntity): ResponseEntity<*> {
        return try {
            val nickname: String = authenticationRequest.nickname
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(nickname, authenticationRequest.password))
            val person: Person = personService.getByNickname(nickname)
                ?: throw UsernameNotFoundException("User with nickname: $nickname not found")
            val token: String = jwtTokenProvider.createToken(nickname, person.roles)
            val response: MutableMap<Any, Any> = HashMap()
            response["nickname"] = nickname
            response["token"] = token
            ResponseEntity.ok<Map<Any, Any>>(response)
        } catch (e: AuthenticationException) {
            throw BadCredentialsException("Invalid nickname or password")
        }
    }

    @PostMapping("registration")
    @Throws(Exception::class)
    fun createUser(@RequestBody createPersonRequest: CreatePersonRequestEntity): ResponseEntity<*> {
//        if (personRepo.existsByNickname(createPersonRequest.getNickname())) {
//            throw Exception("User with same nickname already exists")
//        }
//        if (personRepo.existsByLogin(createPersonRequest.getLogin())) {
//            throw Exception("User with same login already exists")
//        }
//        val localDate = LocalDate.now()
        val person = Person(
            createPersonRequest.name,
            createPersonRequest.nickname,
            passwordEncoder.encode(createPersonRequest.password),
            createPersonRequest.secretWord,
            createPersonRequest.gender,
            createPersonRequest.city
        )
        person.roles.add(roleRepo.findByName("ROLE_USER"))
        personRepo.save(person)
        val response: MutableMap<Any, Any> = HashMap()
        response["nickname"] = person.nickname
        response["password"] = person.password
//        response["date"] = person.getDate()
        return ResponseEntity.ok<Map<Any, Any>>(response)
    }
}
