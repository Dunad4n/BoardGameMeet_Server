package com.example.bgm.controller

import com.example.bgm.controller.dto.*
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.services.EventService
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
class EventController {

    @Autowired
    private lateinit var eventService: EventService

    @Autowired
    private lateinit var requestValidationService: RequestValidationService


    /** Event Мероприятия на главной странице **/
    @GetMapping("/events")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", content = [(Content(mediaType = "application/json", array = (ArraySchema(schema = Schema(implementation = MainPageEventResponseEntity::class)))))])]
    )
    fun allEvents(@RequestParam city: String,
                  @RequestParam(required = false) search: String?,
                  @AuthenticationPrincipal authPerson: JwtPerson?,
                  @PageableDefault() pageable: Pageable
    ): List<MainPageEventResponseEntity>? {
        return eventService.getMainPageEvents(city, search, pageable, authPerson)
    }

    /** Event Мои мероприятия **/
    @GetMapping("/myEvents")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", content = [(Content(mediaType = "application/json", array = (ArraySchema(schema = Schema(implementation = MyEventsResponseEntity::class)))))])]
    )
    fun myEvents(@AuthenticationPrincipal authPerson: JwtPerson, @PageableDefault() pageable: Pageable): List<MyEventsResponseEntity> {
        return eventService.getMyEventsPageEvent(authPerson, pageable)
    }

    /** Event Конкретное мероприятие **/
    @GetMapping("/event/{eventId}")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", content = [Content(schema = Schema(implementation = EventResponseEntity::class), mediaType = "application/json")]),
        ApiResponse(responseCode = "510", description = "Event with id not exist")
    ])
    fun event(@PathVariable eventId: Long, @AuthenticationPrincipal authPerson: JwtPerson): ResponseEntity<*> {
        return eventService.getEvent(eventId, authPerson)
    }

    /** Event создание мероприятия **/
    @PostMapping("/createEvent")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", description = "Ok"),
        ApiResponse(responseCode = "409", description = "The request failed validation")
    ])
    fun createEvent(@RequestBody createEventRequest: CreateEventRequestEntity,
                    @AuthenticationPrincipal authPerson: JwtPerson): ResponseEntity<*> {
        if(!requestValidationService.validate(createEventRequest))
            return ResponseEntity.status(HttpStatus.CONFLICT).body(requestValidationService.getMessage())
        return eventService.createEvent(createEventRequest, authPerson.id)
    }

    /** Event Получить все предметы мероприятия **/
    @GetMapping("/getItemsIn/{eventId}")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", content = [(Content(mediaType = "application/json", array = (ArraySchema(schema = Schema(implementation = ItemResponseEntity::class)))))]),
        ApiResponse(responseCode = "510", description = "Event with id not exist")
    ])
    fun getItems(@PathVariable eventId: Long,
                 @AuthenticationPrincipal authPerson: JwtPerson): ResponseEntity<*> {
        return eventService.getItems(eventId, authPerson)
    }

    /** Event Редактировать предметы мероприятия **/
    @PutMapping("/editItemsIn/{eventId}")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", description = "done"),
        ApiResponse(responseCode = "409", description = "The request failed validation"),
        ApiResponse(responseCode = "510", description = "Event with id not exist")
    ])
    fun editItems(@PathVariable eventId: Long,
                  @RequestBody editItemsRequest: List<EditItemsRequestEntity>,
                  @AuthenticationPrincipal authPerson: JwtPerson): ResponseEntity<*> {
        if(!requestValidationService.validate(editItemsRequest))
            throw ResponseStatusException(
                HttpStatus.CONFLICT, requestValidationService.getMessage()
            )
        return eventService.editItems(eventId, editItemsRequest, authPerson.id)
    }

    /** Event забанить пользователя в мероприятии **/
    @PostMapping("/kickPerson")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", description = "done"),
        ApiResponse(responseCode = "510", description = "Event with id not exist"),
        ApiResponse(responseCode = "511", description = "Person with nickname not exist")
    ])
    fun banPerson(@RequestBody kickPersonRequest: KickPersonRequestEntity,
                  @AuthenticationPrincipal authPerson: JwtPerson): ResponseEntity<*> {
        return eventService.banPerson(kickPersonRequest.eventId, kickPersonRequest.userNickname, authPerson)
    }

    /** Event Удалить мероприятие **/
    @DeleteMapping("/deleteEvent/{eventId}")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", description = "done"),
        ApiResponse(responseCode = "510", description = "Event with id not exist"),
    ])
    fun deleteEvent(@PathVariable eventId: Long, @AuthenticationPrincipal authPerson: JwtPerson): ResponseEntity<*> {
        return eventService.deleteEvent(eventId, authPerson)
    }

    /** Event Редактировать мероприятие **/
    @PutMapping("/updateEvent")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", description = "done"),
        ApiResponse(responseCode = "409", description = "The request failed validation"),
        ApiResponse(responseCode = "510", description = "Event with id not exist")
    ])
    fun updateEvent(@RequestBody request: UpdateEventRequest,
                    @AuthenticationPrincipal authPerson: JwtPerson): ResponseEntity<*> {
        if(!requestValidationService.validate(request))
            throw ResponseStatusException(
                HttpStatus.CONFLICT, requestValidationService.getMessage()
            )
        return eventService.updateEvent(request, authPerson)
    }

    @DeleteMapping("/deleteItemsIn/{eventId}")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", description = "done"),
        ApiResponse(responseCode = "510", description = "Event with id not exist")
    ])
    fun deleteItems(@PathVariable eventId: Long,
                    @AuthenticationPrincipal authPerson: JwtPerson): ResponseEntity<*> {
        return eventService.deleteItems(eventId, authPerson)
    }

    /** Event Отметить предмет **/
    @PutMapping("/markItemIn/{eventId}")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", description = "done"),
        ApiResponse(responseCode = "510", description = "Event with id not exist")
    ])
    fun markItems(@PathVariable eventId: Long,
                  @RequestBody markItemsRequest: MarkItemRequestEntity,
                  @AuthenticationPrincipal authPerson: JwtPerson): ResponseEntity<*> {
        return eventService.markItem(eventId, markItemsRequest, authPerson)
    }
}