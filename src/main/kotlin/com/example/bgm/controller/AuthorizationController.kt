package com.example.bgm.controller

import com.example.bgm.controller.dto.*
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.jwt.JwtTokenProvider
import com.example.bgm.services.AuthService
import com.example.bgm.services.PersonService
import com.example.bgm.services.RequestValidationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@Tag(name = "Контроллер авторизации", description="Действия с аккаунтами пользователей")
class AuthorizationController {

    @Autowired
    private lateinit var authService: AuthService

    @Autowired
    private lateinit var validationService: RequestValidationService

    @Autowired
    private lateinit var personService: PersonService

    @Autowired
    private lateinit var requestValidationService: RequestValidationService

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @PostMapping("/auth/login")
    @Operation(summary = "Вход а аккаунт")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", description = "done"),
        ApiResponse(responseCode = "409", description = "Неверный никнейм или пароль")
    ])
    fun login(@RequestBody authenticationRequest: AuthenticationRequestEntity): ResponseEntity<*> {
        return authService.login(authenticationRequest)
    }

    @PostMapping("exit")
    @Operation(summary = "Выход из аккаунта")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", description = "done"),
    ])
    fun logout(@AuthenticationPrincipal authPerson: JwtPerson) {
        return authService.logout(authPerson)
    }

    @PostMapping("/auth/registration")
    @Operation(summary = "Регистрация")
    @Throws(Exception::class)
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", description = "done"),
        ApiResponse(responseCode = "409", description = "The request failed validation"),
        ApiResponse(responseCode = "409", description = "Такой никнейм уже занят"),
    ])
    fun registration(@RequestBody createPersonRequest: CreatePersonRequestEntity): ResponseEntity<*> {
        if (!validationService.validate(createPersonRequest)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(validationService.getMessage())
        }
        return authService.createPerson(createPersonRequest)
    }
}
