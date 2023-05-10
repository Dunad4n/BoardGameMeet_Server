package com.example.bgm.controller

import com.example.bgm.controller.dto.AuthenticationRequestEntity
import com.example.bgm.controller.dto.AuthenticationResponseEntity
import com.example.bgm.controller.dto.CreatePersonRequestEntity
import com.example.bgm.repositories.RoleRepo
import com.example.bgm.services.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthorizationController {

    @Autowired
    lateinit var authService: AuthService

    @Autowired
    lateinit var roleRepo: RoleRepo


    /** Авторизация **/
    @PostMapping("login")
    fun login(@RequestBody authenticationRequest: AuthenticationRequestEntity): AuthenticationResponseEntity {
        return authService.login(authenticationRequest)
    }

    /** Регистрация **/
    @PostMapping("registration")
    @Throws(Exception::class)
    fun registration(@RequestBody createPersonRequest: CreatePersonRequestEntity) {
        authService.createPerson(createPersonRequest, roleRepo.findByName("ROLE_USER"))
    }
}
