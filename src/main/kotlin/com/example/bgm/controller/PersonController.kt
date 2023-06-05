package com.example.bgm.controller

import com.example.bgm.controller.dto.*
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.jwt.JwtTokenProvider
import com.example.bgm.services.PersonService
import com.example.bgm.services.RequestValidationService
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
    fun profile(@PathVariable nickname: String): ProfileResponseEntity {
        return personService.getProfile(nickname)
    }

    @GetMapping("/ownProfile")
    fun ownProfile(@AuthenticationPrincipal authPerson: JwtPerson): ProfileResponseEntity {
        return personService.getProfile(authPerson.username)
    }

    /** Person Покинуть мероприятие **/
    @PostMapping("/leaveEvent/{eventId}")
    fun leaveEvent(@PathVariable eventId: Long,
                   @AuthenticationPrincipal authPerson: JwtPerson
    ) {
        if (authPerson.id == null) {
            throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "UNAUTHORIZED"
            )
        }
        personService.leaveFromEvent(authPerson.id, eventId)
    }

    /** Person Присоединиться к мероприятию **/
    @PostMapping("/joinEvent/{eventId}")
    fun joinEvent(@PathVariable eventId: Long,
                  @AuthenticationPrincipal authPerson: JwtPerson
    ) {
        if (authPerson.id == null) {
            throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "UNAUTHORIZED"
            )
        }
        personService.joinToEvent(authPerson.id, eventId)
    }

    /** Person Все участники **/
    @GetMapping("/getAllMembersIn/{eventId}")
    fun getAllMembers(@PathVariable eventId: Long, @PageableDefault() pageable: Pageable): ArrayList<MemberResponseEntity> {
        return personService.getAllMembers(eventId, pageable)
    }

    /** Person Удалить пользователя **/
    @DeleteMapping("/admin/deletePerson/{nickname}")
    fun deletePerson(@PathVariable nickname: String, @AuthenticationPrincipal authPerson: JwtPerson) {
        personService.deletePerson(nickname, authPerson)
    }

    /** Person Редактировать профиль **/
    @PutMapping("/updatePerson")
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
    fun validateSecretWord(@RequestBody validateSecretWordRequest: ValidateSecretWordRequestEntity
    ): ResponseEntity<String> {
        return personService.validateSecretWord(validateSecretWordRequest.secretWord,
                                                validateSecretWordRequest.nickname)
    }

    /** Person Сменить пароль **/
    @PutMapping("/changePassword")
    fun changePassword(@RequestBody changePasswordRequest: ChangePasswordRequestEntity,
    ) {
        return personService.changePassword(changePasswordRequest.newPassword,
            changePasswordRequest.repeatNewPassword, changePasswordRequest.nickname)
    }

    @GetMapping("/isProfileOf/{nickname}")
    fun isMyProfile(@PathVariable(name = "nickname") nickname: String,
                    @AuthenticationPrincipal authPerson: JwtPerson): IsMyProfileResponseEntity {
        return personService.isMyProfile(nickname, authPerson)
    }

    @PostMapping("/verifyToken")
    fun verifyToken(@RequestBody verifyTokenRequest: VerifyTokenRequestEntity): Boolean {
        return personService.verifyToken(verifyTokenRequest.token, verifyTokenRequest.nickname)
    }

}