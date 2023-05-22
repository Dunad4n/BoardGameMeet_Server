package com.example.bgm.controller

import com.example.bgm.controller.dto.*
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.services.EventService
import com.example.bgm.services.RequestValidationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
class EventController {

    @Autowired
    lateinit var eventService: EventService

    @Autowired
    lateinit var requestValidationService: RequestValidationService

    /** Event Мероприятия на главной странице **/
    @GetMapping("/events")
    fun allEvents(@RequestParam city: String,
                  @RequestParam(required = false) search: String?,
                  @AuthenticationPrincipal authPerson: JwtPerson?,
                  @PageableDefault() pageable: Pageable
    ): List<MainPageEventResponseEntity>? {
        return eventService.getMainPageEvents(city, search, pageable, authPerson)
    }

    /** Event Мои мероприятия **/
    @GetMapping("/myEvents")
    fun myEvents(@AuthenticationPrincipal authPerson: JwtPerson, @PageableDefault() pageable: Pageable): List<MyEventsResponseEntity> {
        return eventService.getMyEventsPageEvent(authPerson, pageable)
    }

    /** Event Конкретное мероприятие **/
    @GetMapping("/event/{eventId}")
    fun event(@PathVariable eventId: Long): EventResponseEntity {
        return eventService.getEvent(eventId)
    }

    /** Event создание мероприятия **/
    @PostMapping("/createEvent")
    fun createEvent(@RequestBody createEventRequest: CreateEventRequestEntity,
                    @AuthenticationPrincipal authPerson: JwtPerson) {
        if(!requestValidationService.validate(createEventRequest))
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST, requestValidationService.getMessage()
            )
        eventService.createEvent(createEventRequest, authPerson.id)
    }

    /** Event Получить все предметы мероприятия **/
    @GetMapping("/getItemsIn/{eventId}")
    fun getItems(@PathVariable eventId: Long, @PageableDefault() pageable: Pageable): List<ItemResponseEntity> {
        return eventService.getItems(eventId, pageable)
    }

    /** Event Редактировать предметы мероприятия **/
    @PutMapping("/editItemsIn/{eventId}")
    fun editItems(@PathVariable eventId: Long,
                  @RequestBody editItemsRequest: List<EditItemsRequestEntity>,
                  @AuthenticationPrincipal authPerson: JwtPerson) {
        if(!requestValidationService.validate(editItemsRequest))
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST, requestValidationService.getMessage()
            )
        return eventService.editItems(eventId, editItemsRequest, authPerson.id)
    }

    /** Event забанить пользователя в мероприятии **/
    @PostMapping("/banPerson")
    fun banPerson(@RequestBody eventId: Long,
                  @RequestBody userNickname: String,
                  @AuthenticationPrincipal authPerson: JwtPerson) {
        eventService.banPerson(eventId, userNickname, authPerson)
    }

    /** Event Удалить мероприятие **/
    @DeleteMapping("/deleteEvent/{eventId}")
    fun deleteEvent(@PathVariable eventId: Long, @AuthenticationPrincipal authPerson: JwtPerson) {
        eventService.deleteEvent(eventId, authPerson)
    }

    /** Event Редактировать мероприятие **/
    @PutMapping("/updateEvent")
    fun updateEvent(@RequestBody request: UpdateEventRequest) {
        if(!requestValidationService.validate(request))
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST, requestValidationService.getMessage()
            )
        eventService.updateEvent(request)
    }

    /** Event Отметить предмет **/
    @PutMapping("/markItemsIn/{eventId}")
    fun markItems(@PathVariable eventId: Long,
                  @RequestBody markItemsRequest: MarkItemsRequestEntity) {
        eventService.markItems(eventId, markItemsRequest)
    }
}