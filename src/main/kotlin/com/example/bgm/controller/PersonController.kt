package com.example.bgm.controller

import com.example.bgm.controller.dto.*
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.jwt.JwtTokenProvider
import com.example.bgm.services.PersonService
import com.example.bgm.services.RequestValidationService
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@Tag(name = "Контроллер пользователей", description="Действия пользователя")
class PersonController {

    @Autowired
    lateinit var personService: PersonService

    @Autowired
    lateinit var requestValidationService: RequestValidationService

    @Autowired
    lateinit var jwtTokenProvider: JwtTokenProvider

    /** Person Профиль пользователя **/
    @GetMapping("/profile/{nickname}")
    @Operation(summary = "Получения профиля пользователя")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", content = [Content(schema = Schema(implementation = ProfileResponseEntity::class), mediaType = "application/json")]),
        ApiResponse(responseCode = "511", description = "Person with nickname not exist")
    ])
    fun profile(@PathVariable nickname: String): ResponseEntity<*> {
        return personService.getProfile(nickname)
    }

    @GetMapping("/ownProfile")
    @Operation(summary = "Получения своего профиля")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", content = [Content(schema = Schema(implementation = ProfileResponseEntity::class), mediaType = "application/json")]),
        ApiResponse(responseCode = "511", description = "Person with nickname not exist")
    ])
    fun ownProfile(@AuthenticationPrincipal authPerson: JwtPerson): ResponseEntity<*> {
        return personService.getProfile(authPerson.username)
    }

    /** Person Покинуть мероприятие **/
    @PostMapping("/leaveEvent/{eventId}")
    @Operation(summary = "Получения профиля пользователя")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "done"),
        ApiResponse(responseCode = "510", description = "Event with id not exist")
    ])
    fun leaveEvent(@PathVariable@Parameter(description = "Id мероприятия") eventId: Long,
                   @AuthenticationPrincipal authPerson: JwtPerson
    ): ResponseEntity<*> {
        if (authPerson.id == null) {
            throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "UNAUTHORIZED"
            )
        }
        return personService.leaveFromEvent(authPerson.id, eventId)
    }

    @PostMapping("/joinEvent/{eventId}")
    @Operation(summary = "Присоединение к мероприятию")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "done"),
        ApiResponse(responseCode = "510", description = "Event with id not exist")
    ])
    fun joinEvent(@PathVariable@Parameter(description = "Id мероприятия") eventId: Long,
                  @AuthenticationPrincipal authPerson: JwtPerson
    ): ResponseEntity<*> {
        if (authPerson.id == null) {
            throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "UNAUTHORIZED"
            )
        }
        return personService.joinToEvent(authPerson.id, eventId)
    }

    /** Person Все участники **/
    @GetMapping("/getAllMembersIn/{eventId}")
    @Operation(summary = "Получения списка участников", description = "В пагинации указывается только page и size")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", content = [(Content(mediaType = "application/json", array = (ArraySchema(schema = Schema(implementation = MemberResponseEntity::class)))))]),
        ApiResponse(responseCode = "510", description = "Event with id not exist")
    ])
    fun getAllMembers(@PathVariable@Parameter(description = "Id мероприятия") eventId: Long,
                      @PageableDefault() pageable: Pageable,
                      @AuthenticationPrincipal authPerson: JwtPerson): ResponseEntity<*> {
        return personService.getAllMembers(eventId, pageable, authPerson)
    }

    /** Person Удалить пользователя **/
    @DeleteMapping("/admin/deletePerson/{nickname}")
    @Operation(summary = "Удаление пользователя из мероприятия")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "done"),
        ApiResponse(responseCode = "511", description = "Person with nickname not exist")
    ])
    fun deletePerson(@PathVariable@Parameter(description = "Ник пользователя") nickname: String,
                     @AuthenticationPrincipal authPerson: JwtPerson): ResponseEntity<*> {
        return personService.deletePerson(nickname, authPerson)
    }

    /** Person Редактировать профиль **/
    @PutMapping("/updatePerson")
    @Operation(summary = "Редактирование профиля")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "done"),
        ApiResponse(responseCode = "409", description = "The request failed validation"),
    ])
    fun updatePerson(@RequestBody request: UpdatePersonRequestEntity,
                     @AuthenticationPrincipal authPerson: JwtPerson
    ): ResponseEntity<*> {
        if(!requestValidationService.validate(request))
            return ResponseEntity.status(HttpStatus.CONFLICT).body(requestValidationService.getMessage())
        return personService.updatePerson(request, authPerson, jwtTokenProvider)
    }

    /** Person Проверить валидность секретного слова **/
    @PostMapping("/validateSecretWord")
    @Operation(summary = "Проверка валидности секретного слова")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "correct secret word"),
        ApiResponse(responseCode = "409", description = "Неверное секретное слово или никнейм"),
    ])
    fun validateSecretWord(@RequestBody validateSecretWordRequest: ValidateSecretWordRequestEntity
    ): ResponseEntity<String> {
        return personService.validateSecretWord(validateSecretWordRequest.secretWord,
                                                validateSecretWordRequest.nickname)
    }

    /** Person Сменить пароль **/
    @PutMapping("/changePassword")
    @Operation(summary = "Смена пароля")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "done"),
    ])
    fun changePassword(@RequestBody changePasswordRequest: ChangePasswordRequestEntity) {
        return personService.changePassword(changePasswordRequest.nickname, changePasswordRequest.newPassword,
            changePasswordRequest.repeatNewPassword)
    }

    @GetMapping("/isProfileOf/{nickname}")
    @Operation(summary = "Проверка соответствия профиля профилю пользователя, делающего запрос")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", content = [Content(schema = Schema(implementation = IsMyProfileResponseEntity::class), mediaType = "application/json")]),
    ])
    fun isMyProfile(@PathVariable(name = "nickname") nickname: String,
                    @AuthenticationPrincipal authPerson: JwtPerson): IsMyProfileResponseEntity {
        return personService.isMyProfile(nickname, authPerson)
    }

    @PostMapping("/verifyToken")
    @Operation(summary = "Проверка токена")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", content = [Content(schema = Schema(implementation = Boolean::class), mediaType = "application/json")]),
    ])
    fun verifyToken(@RequestBody verifyTokenRequest: VerifyTokenRequestEntity): Boolean {
        return personService.verifyToken(verifyTokenRequest.token, verifyTokenRequest.nickname)
    }

    @GetMapping("/isMemberOfEvent/{eventId}")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", content = [Content(schema = Schema(implementation = ResponseEntity::class), mediaType = "application/json")]),
        ApiResponse(responseCode = "510", description = "Event with id not exist")
    ])
    fun isMemberOfEvent(@PathVariable eventId: Long,
                        @AuthenticationPrincipal authPerson: JwtPerson): ResponseEntity<*> {
        return personService.isMemberOfEvent(eventId, authPerson)
    }

}