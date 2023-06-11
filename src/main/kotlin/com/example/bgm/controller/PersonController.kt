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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
class PersonController {

    @Autowired
    lateinit var personService: PersonService

    @Autowired
    lateinit var requestValidationService: RequestValidationService

    @Autowired
    lateinit var jwtTokenProvider: JwtTokenProvider

    /** Person Профиль пользователя **/
    @GetMapping("/profile/{nickname}")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", content = [Content(schema = Schema(implementation = ProfileResponseEntity::class), mediaType = "application/json")]),
        ApiResponse(responseCode = "511", description = "Person with nickname not exist")
    ])
    fun profile(@PathVariable nickname: String): ResponseEntity<*> {
        return personService.getProfile(nickname)
    }

    @GetMapping("/ownProfile")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", content = [Content(schema = Schema(implementation = ProfileResponseEntity::class), mediaType = "application/json")]),
        ApiResponse(responseCode = "511", description = "Person with nickname not exist")
    ])
    fun ownProfile(@AuthenticationPrincipal authPerson: JwtPerson): ResponseEntity<*> {
        return personService.getProfile(authPerson.username)
    }

    /** Person Покинуть мероприятие **/
    @PostMapping("/leaveEvent/{eventId}")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", description = "done"),
        ApiResponse(responseCode = "510", description = "Event with id not exist")
    ])
    fun leaveEvent(@PathVariable eventId: Long,
                   @AuthenticationPrincipal authPerson: JwtPerson
    ): ResponseEntity<*> {
        if (authPerson.id == null) {
            throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "UNAUTHORIZED"
            )
        }
        return personService.leaveFromEvent(authPerson.id, eventId)
    }

    /** Person Присоединиться к мероприятию **/
    @PostMapping("/joinEvent/{eventId}")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", description = "done"),
        ApiResponse(responseCode = "510", description = "Event with id not exist")
    ])
    fun joinEvent(@PathVariable eventId: Long,
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
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", content = [(Content(mediaType = "application/json", array = (ArraySchema(schema = Schema(implementation = MemberResponseEntity::class)))))]),
        ApiResponse(responseCode = "510", description = "Event with id not exist")
    ])
    fun getAllMembers(@PathVariable eventId: Long, @PageableDefault() pageable: Pageable): ResponseEntity<*> {
        return personService.getAllMembers(eventId, pageable)
    }

    /** Person Удалить пользователя **/
    @DeleteMapping("/admin/deletePerson/{nickname}")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", description = "done"),
        ApiResponse(responseCode = "511", description = "Person with nickname not exist")
    ])
    fun deletePerson(@PathVariable nickname: String, @AuthenticationPrincipal authPerson: JwtPerson): ResponseEntity<*> {
        return personService.deletePerson(nickname, authPerson)
    }

    /** Person Редактировать профиль **/
    @PutMapping("/updatePerson")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", description = "done"),
        ApiResponse(responseCode = "409", description = "The request failed validation"),
    ])
    fun updatePerson(@RequestBody request: UpdatePersonRequestEntity,
                     @AuthenticationPrincipal authPerson: JwtPerson
    ): ResponseEntity<*> {
        if(!requestValidationService.validate(request))
            throw ResponseStatusException(
                HttpStatus.CONFLICT, requestValidationService.getMessage()
            )
        return personService.updatePerson(request, authPerson, jwtTokenProvider)
    }

    /** Person Проверить валидность секретного слова **/
    @PostMapping("/validateSecretWord")
    @ApiResponses( value = [
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
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", description = "done"),
    ])
    fun changePassword(@RequestBody changePasswordRequest: ChangePasswordRequestEntity) {
        return personService.changePassword(changePasswordRequest.nickname, changePasswordRequest.newPassword,
            changePasswordRequest.repeatNewPassword)
    }

    @GetMapping("/isProfileOf/{nickname}")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", content = [Content(schema = Schema(implementation = IsMyProfileResponseEntity::class), mediaType = "application/json")]),
    ])
    fun isMyProfile(@PathVariable(name = "nickname") nickname: String,
                    @AuthenticationPrincipal authPerson: JwtPerson): IsMyProfileResponseEntity {
        return personService.isMyProfile(nickname, authPerson)
    }

    @PostMapping("/verifyToken")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", content = [Content(schema = Schema(implementation = Boolean::class), mediaType = "application/json")]),
    ])
    fun verifyToken(@RequestBody verifyTokenRequest: VerifyTokenRequestEntity): Boolean {
        return personService.verifyToken(verifyTokenRequest.token, verifyTokenRequest.nickname)
    }

    @GetMapping("/isMemberOfEvent/{eventId}")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", content = [Content(schema = Schema(implementation = ResponseEntity::class), mediaType = "application/json")]),
        ApiResponse(responseCode = "510", description = "Event with id not exist")
    ])
    fun isMemberOfEvent(@PathVariable eventId: Long,
                        @AuthenticationPrincipal authPerson: JwtPerson): ResponseEntity<*> {
        return personService.isMemberOfEvent(eventId, authPerson)
    }

}