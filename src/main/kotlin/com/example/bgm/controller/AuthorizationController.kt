package com.example.bgm.controller

import com.example.bgm.controller.dto.*
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.jwt.JwtTokenProvider
import com.example.bgm.services.AuthService
import com.example.bgm.services.PersonService
import com.example.bgm.services.RequestValidationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
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

    @Autowired private lateinit var authService: AuthService
    @Autowired private lateinit var validationService: RequestValidationService

    @PostMapping("/auth/login")
    @Operation(summary = "Вход а аккаунт")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", content = [(Content(mediaType = "application/json", schema = Schema(implementation = AuthenticationResponseEntity::class)))]),
        ApiResponse(responseCode = "409", description = "Неверный никнейм или пароль")
    ])
    fun login(@RequestBody authenticationRequest: AuthenticationRequestEntity): ResponseEntity<*> {
        return authService.login(authenticationRequest)
    }

    @PostMapping("exit")
    @Operation(summary = "Выход из аккаунта")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200"),
    ])
    fun logout(@AuthenticationPrincipal authPerson: JwtPerson) {
        return authService.logout(authPerson)
    }

    @PostMapping("/auth/registration")
    @Operation(summary = "Регистрация")
    @Throws(Exception::class)
    @ApiResponses( value = [
        ApiResponse(responseCode = "200"),
        ApiResponse(responseCode = "409", description = "Некорректный запрос"),
        ApiResponse(responseCode = "409", description = "Такой никнейм уже занят"),
    ])
    fun registration(@RequestBody createPersonRequest: CreatePersonRequestEntity): ResponseEntity<*> {
        if (!validationService.validate(createPersonRequest)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(validationService.getMessage())
        }
        return authService.createPerson(createPersonRequest)
    }
}
