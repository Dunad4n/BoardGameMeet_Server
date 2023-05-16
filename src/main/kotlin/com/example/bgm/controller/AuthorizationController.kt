package com.example.bgm.controller

import com.example.bgm.controller.dto.AuthenticationRequestEntity
import com.example.bgm.controller.dto.AuthenticationResponseEntity
import com.example.bgm.controller.dto.CreatePersonRequestEntity
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.repositories.RoleRepo
import com.example.bgm.services.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthorizationController {

    @Autowired
    lateinit var authService: AuthService

    @Autowired
    lateinit var roleRepo: RoleRepo


    /** Авторизация **/
    @PostMapping("/auth/login")
    fun login(@RequestBody authenticationRequest: AuthenticationRequestEntity): AuthenticationResponseEntity {
        return authService.login(authenticationRequest)
    }

    /** Выход из аккаунта **/
    @PostMapping("exit")
    fun logout(@AuthenticationPrincipal authPerson: JwtPerson) {
        return authService.logout(authPerson)
    }

    /** Регистрация **/
    @PostMapping("/auth/registration")
    @Throws(Exception::class)
    fun registration(@RequestBody createPersonRequest: CreatePersonRequestEntity) {
        return authService.createPerson(createPersonRequest, roleRepo.findByName("ROLE_USER"))
    }
}
