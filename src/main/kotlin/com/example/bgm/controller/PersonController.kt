package com.example.bgm.controller

import com.example.bgm.controller.dto.*
import com.example.bgm.jwt.JwtPerson
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

    /** Person Профиль пользователя **/
    @GetMapping("/profile/{nickname}")
    fun profile(@PathVariable nickname: String,
                @AuthenticationPrincipal authPerson: JwtPerson): ProfileResponseEntity {
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
    @PostMapping("/updatePerson")
    fun updatePerson(@RequestBody request: UpdatePersonRequestEntity,
                     @AuthenticationPrincipal authPerson: JwtPerson
    ) {
        if(!requestValidationService.validate(request))
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST, requestValidationService.getMessage()
            )
        personService.updatePerson(request, authPerson)
    }

    /** Person Проверить валидность секретного слова **/
    @PostMapping("/validateSecretWord")
    fun validateSecretWord(@RequestBody validateSecretWordRequest: ValidateSecretWordRequestEntity,
                           @AuthenticationPrincipal authPerson: JwtPerson
    ): ResponseEntity<String> {
        return personService.validateSecretWord(validateSecretWordRequest.secretWord, authPerson)
    }

    /** Person Сменить пароль **/
    @PutMapping("/changePassword")
    fun changePassword(@RequestBody changePasswordRequest: ChangePasswordRequestEntity,
                       @AuthenticationPrincipal authPerson: JwtPerson
    ) {
        return personService.changePassword(changePasswordRequest.newPassword,
            changePasswordRequest.repeatNewPassword,
            authPerson)
    }

}