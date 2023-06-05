package com.example.bgm.controller

import com.example.bgm.controller.dto.*
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.jwt.JwtTokenProvider
import com.example.bgm.services.AuthService
import com.example.bgm.services.PersonService
import com.example.bgm.services.RequestValidationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
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
    lateinit var authService: AuthService

    @Autowired
    lateinit var personService: PersonService

    @Autowired
    lateinit var requestValidationService: RequestValidationService

    @Autowired
    lateinit var jwtTokenProvider: JwtTokenProvider

    @PostMapping("/auth/login")
    @Operation(summary = "Вход а аккаунт")
    fun login(@RequestBody authenticationRequest: AuthenticationRequestEntity): ResponseEntity<*> {
        return authService.login(authenticationRequest)
    }

    /** Выход из аккаунта **/
    @PostMapping("exit")
    @Operation(summary = "Выход из аккаунта")
    fun logout(@AuthenticationPrincipal authPerson: JwtPerson) {
        return authService.logout(authPerson)
    }

    /** Регистрация **/
    @PostMapping("/auth/registration")
    @Operation(summary = "Регистрация")
    @Throws(Exception::class)
    fun registration(@RequestBody createPersonRequest: CreatePersonRequestEntity): ResponseEntity<*> {
        if (!requestValidationService.validate(createPersonRequest)) {
            throw ResponseStatusException (
                HttpStatus.CONFLICT, requestValidationService.getMessage()
            )
        }
        return authService.createPerson(createPersonRequest)
    }

    @GetMapping("/profile/{nickname}")
    @Operation(summary = "Получения профиля пользователя")
    fun profile(@PathVariable@Parameter(description = "Ник пользователя") nickname: String): ProfileResponseEntity {
        return personService.getProfile(nickname)
    }

    @GetMapping("/ownProfile")
    @Operation(summary = "Получения своего профиля")
    fun ownProfile(@AuthenticationPrincipal authPerson: JwtPerson): ProfileResponseEntity {
        return personService.getProfile(authPerson.username)
    }

    @PutMapping("/updatePerson")
    @Operation(summary = "Редактирование профиля")
    fun updatePerson(@RequestBody request: UpdatePersonRequestEntity,
                     @AuthenticationPrincipal authPerson: JwtPerson
    ): ResponseEntity<*> {
        if(!requestValidationService.validate(request))
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST, requestValidationService.getMessage()
            )
        return personService.updatePerson(request, authPerson, jwtTokenProvider)
    }

     @PostMapping("/validateSecretWord")
    @Operation(summary = "Проверка валидности секретного слова")
    fun validateSecretWord(@RequestBody validateSecretWordRequest: ValidateSecretWordRequestEntity
    ): ResponseEntity<String> {
        return personService.validateSecretWord(validateSecretWordRequest.secretWord,
            validateSecretWordRequest.nickname)
    }

    @PutMapping("/changePassword")
    @Operation(summary = "Смена пароля")
    fun changePassword(@RequestBody changePasswordRequest: ChangePasswordRequestEntity,
    ) {
        return personService.changePassword(changePasswordRequest.newPassword,
            changePasswordRequest.repeatNewPassword, changePasswordRequest.nickname)
    }

    @GetMapping("/isProfileOf/{nickname}")
    @Operation(summary = "Проверка соответствия профиля профилю пользователя, делающего запрос")
    fun isMyProfile(@PathVariable(name = "nickname")@Parameter(description = "Ник пользователя") nickname: String,
                    @AuthenticationPrincipal authPerson: JwtPerson): IsMyProfileResponseEntity {
        return personService.isMyProfile(nickname, authPerson)
    }

    @PostMapping("/verifyToken")
    @Operation(summary = "Проверка токена")
    fun verifyToken(@RequestBody verifyTokenRequest: VerifyTokenRequestEntity): Boolean {
        return personService.verifyToken(verifyTokenRequest.token, verifyTokenRequest.nickname)
    }
}
