package com.example.bgm.controller

import com.example.bgm.controller.dto.*
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.jwt.JwtTokenProvider
import com.example.bgm.services.PersonService
import com.example.bgm.services.RequestValidationService
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

    @PostMapping("/leaveEvent/{eventId}")
    @Operation(summary = "Получения профиля пользователя")
    fun leaveEvent(@PathVariable@Parameter(description = "Id мероприятия") eventId: Long,
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
    @Operation(summary = "Присоединение к мероприятию")
    fun joinEvent(@PathVariable@Parameter(description = "Id мероприятия") eventId: Long,
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
    @Operation(summary = "Получения списка участников", description = "В пагинации указывается только page и size")
    fun getAllMembers(@PathVariable@Parameter(description = "Id мероприятия") eventId: Long, @PageableDefault()@Parameter(description = "Пагинация") pageable: Pageable): ArrayList<MemberResponseEntity> {
        return personService.getAllMembers(eventId, pageable)
    }

    /** Person Удалить пользователя **/
    @DeleteMapping("/admin/deletePerson/{nickname}")
    @Operation(summary = "Удаление пользователя из мероприятия")
    fun deletePerson(@PathVariable@Parameter(description = "Ник пользователя") nickname: String, @AuthenticationPrincipal authPerson: JwtPerson) {
        personService.deletePerson(nickname, authPerson)
    }
}