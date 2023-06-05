package com.example.bgm.controller

import com.example.bgm.controller.dto.AuthenticationRequestEntity
import com.example.bgm.controller.dto.AuthenticationResponseEntity
import com.example.bgm.controller.dto.CreatePersonRequestEntity
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.repositories.RoleRepo
import com.example.bgm.services.AuthService
import com.example.bgm.services.RequestValidationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class AuthorizationController {

    @Autowired
    private lateinit var authService: AuthService

    @Autowired
    private lateinit var validationService: RequestValidationService

    /** Авторизация **/
    @PostMapping("/auth/login")
    fun login(@RequestBody authenticationRequest: AuthenticationRequestEntity): ResponseEntity<*> {
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
    fun registration(@RequestBody createPersonRequest: CreatePersonRequestEntity): ResponseEntity<*> {
        if (!validationService.validate(createPersonRequest)) {
            throw ResponseStatusException (
                HttpStatus.CONFLICT, validationService.getMessage()
            )
        }
        return authService.createPerson(createPersonRequest)
    }
}
