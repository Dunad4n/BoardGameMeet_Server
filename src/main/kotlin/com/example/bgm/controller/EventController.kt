package com.example.bgm.controller

import com.example.bgm.controller.dto.*
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.services.EventService
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
@Tag(name = "Контроллер мероприятий", description="Все операции с мероприятиями")
class EventController {

    @Autowired
    private lateinit var eventService: EventService

    @Autowired
    private lateinit var requestValidationService: RequestValidationService


    /** Event Мероприятия на главной странице **/
    @GetMapping("/events")
    @Operation(summary = "Список всех мероприятий", description = "В пагинации указывается только page и size")
    fun allEvents(@RequestParam @Parameter(description = "Город пользователя") city: String,
                  @RequestParam(required = false) @Parameter(description = "Поиск по названию") search: String?,
                  @AuthenticationPrincipal authPerson: JwtPerson?,
                  @PageableDefault()@Parameter(description = "Пагинация") pageable: Pageable
    ): List<MainPageEventResponseEntity>? {
        return eventService.getMainPageEvents(city, search, pageable, authPerson)
    }

    /** Event Мои мероприятия **/
    @GetMapping("/myEvents")
    @Operation(summary = "Список мероприятий пользователя", description = "В пагинации указывается только page и size")
    fun myEvents(@AuthenticationPrincipal authPerson: JwtPerson, @PageableDefault()@Parameter(description = "Пагинация") pageable: Pageable): List<MyEventsResponseEntity> {
        return eventService.getMyEventsPageEvent(authPerson, pageable)
    }

    /** Event Конкретное мероприятие **/
    @GetMapping("/event/{eventId}")
    @Operation(summary = "Информация о мероприятии")
    fun event(@PathVariable@Parameter(description = "Id мероприятия") eventId: Long, @AuthenticationPrincipal authPerson: JwtPerson): EventResponseEntity {
        return eventService.getEvent(eventId, authPerson)
    }

    /** Event создание мероприятия **/
    @PostMapping("/createEvent")
    @Operation(summary = "Создание мероприятия")
    fun createEvent(@RequestBody createEventRequest: CreateEventRequestEntity,
                    @AuthenticationPrincipal authPerson: JwtPerson): ResponseEntity<*> {
        if(!requestValidationService.validate(createEventRequest))
            return ResponseEntity.status(HttpStatus.CONFLICT).body(requestValidationService.getMessage())
        return eventService.createEvent(createEventRequest, authPerson.id)
    }

    /** Event Получить все предметы мероприятия **/
    @GetMapping("/getItemsIn/{eventId}")
    @Operation(summary = "Список всех предметов мероприятия")
    fun getItems(@PathVariable@Parameter(description = "Id мероприятия") eventId: Long,
                 @AuthenticationPrincipal authPerson: JwtPerson): List<ItemResponseEntity> {
        return eventService.getItems(eventId, authPerson)
    }

    /** Event Редактировать предметы мероприятия **/
    @PutMapping("/editItemsIn/{eventId}")
    @Operation(summary = "Редактирование предметов мероприятия")
    fun editItems(@PathVariable@Parameter(description = "Id мероприятия") eventId: Long,
                  @RequestBody editItemsRequest: List<EditItemsRequestEntity>,
                  @AuthenticationPrincipal authPerson: JwtPerson) {
        if(!requestValidationService.validate(editItemsRequest))
            throw ResponseStatusException(
                HttpStatus.CONFLICT, requestValidationService.getMessage()
            )
        return eventService.editItems(eventId, editItemsRequest, authPerson.id)
    }

    /** Event забанить пользователя в мероприятии **/
    @PostMapping("/kickPerson")
    @Operation(summary = "Забанить пользователя в мероприятии")
    fun banPerson(@RequestBody kickPersonRequest: KickPersonRequestEntity,
                  @AuthenticationPrincipal authPerson: JwtPerson) {
        eventService.banPerson(kickPersonRequest.eventId, kickPersonRequest.userNickname, authPerson)
    }

    /** Event Удалить мероприятие **/
    @DeleteMapping("/deleteEvent/{eventId}")
    @Operation(summary = "Удаление мероприятия")
    fun deleteEvent(@PathVariable@Parameter(description = "Id мероприятия") eventId: Long, @AuthenticationPrincipal authPerson: JwtPerson) {
        eventService.deleteEvent(eventId, authPerson)
    }

    /** Event Редактировать мероприятие **/
    @PutMapping("/updateEvent")
    @Operation(summary = "Редактирование мероприятия")
    fun updateEvent(@RequestBody request: UpdateEventRequest,
                    @AuthenticationPrincipal authPerson: JwtPerson): ResponseEntity<*> {
        if(!requestValidationService.validate(request))
            throw ResponseStatusException(
                HttpStatus.CONFLICT, requestValidationService.getMessage()
            )
        return eventService.updateEvent(request, authPerson)
    }

    @DeleteMapping("/deleteItemsIn/{eventId}")
    @Operation(summary = "Удаление предметов мероприяитя")
    fun deleteItems(@PathVariable@Parameter(description = "Id мероприятия") eventId: Long,
                    @AuthenticationPrincipal authPerson: JwtPerson) {
        eventService.deleteItems(eventId, authPerson)
    }

    /** Event Отметить предмет **/
    @PutMapping("/markItemIn/{eventId}")
    @Operation(summary = "Отметить предмет")
    fun markItems(@PathVariable@Parameter(description = "Id мероприятия") eventId: Long,
                  @RequestBody markItemsRequest: MarkItemRequestEntity,
                  @AuthenticationPrincipal authPerson: JwtPerson) {
        eventService.markItem(eventId, markItemsRequest, authPerson)
    }
}