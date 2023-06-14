package com.example.bgm.controller

import com.example.bgm.entities.dto.*
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.services.EventService
import com.example.bgm.services.RequestValidationService
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "Контроллер мероприятий", description="Все операции с мероприятиями")
class EventController {

    @Autowired
    private lateinit var eventService: EventService

    @Autowired
    private lateinit var requestValidationService: RequestValidationService


    @GetMapping("/events")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", content = [(Content(mediaType = "application/json", array = (ArraySchema(schema = Schema(implementation = MainPageEventResponseEntity::class)))))])]
    )
    @Operation(summary = "Список всех мероприятий",
               description = "В пагинации указывается только page и size",
               security = [SecurityRequirement(name = "bearer-key")])
    fun allEvents(@RequestParam @Parameter(description = "Город пользователя") city: String,
                  @RequestParam(required = false) @Parameter(description = "Поиск по названию") search: String?,
                  @AuthenticationPrincipal authPerson: JwtPerson?,
                  @PageableDefault()@Parameter(description = "Пагинация") pageable: Pageable
    ): List<MainPageEventResponseEntity>? {
        return eventService.getMainPageEvents(city, search, pageable, authPerson)
    }

    @GetMapping("/myEvents")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", content = [(Content(mediaType = "application/json", array = (ArraySchema(schema = Schema(implementation = MyEventsResponseEntity::class)))))])]
    )
    @Operation(summary = "Список мероприятий пользователя",
               description = "В пагинации указывается только page и size",
               security = [SecurityRequirement(name = "bearer-key")])
    fun myEvents(@AuthenticationPrincipal authPerson: JwtPerson,
                 @PageableDefault()@Parameter(description = "Пагинация") pageable: Pageable): List<MyEventsResponseEntity> {
        return eventService.getMyEventsPageEvent(authPerson, pageable)
    }

    /** Event Конкретное мероприятие **/
    @GetMapping("/event/{eventId}")
    @Operation(summary = "Информация о мероприятии", security = [SecurityRequirement(name = "bearer-key")])
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", content = [Content(schema = Schema(implementation = EventResponseEntity::class), mediaType = "application/json")]),
        ApiResponse(responseCode = "471", description = "Мероприятия с таким id не существует")
    ])
    fun event(@PathVariable@Parameter(description = "Id мероприятия") eventId: Long,
              @AuthenticationPrincipal authPerson: JwtPerson): ResponseEntity<*> {
        return eventService.getEvent(eventId, authPerson)
    }


    @PostMapping("/createEvent")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", description = "Ok"),
        ApiResponse(responseCode = "409", description = "Некорректный запрос")
    ])
    @Operation(summary = "Создание мероприятия", security = [SecurityRequirement(name = "bearer-key")])
    fun createEvent(@RequestBody createEventRequest: CreateEventRequestEntity,
                    @AuthenticationPrincipal authPerson: JwtPerson): ResponseEntity<*> {
        if(!requestValidationService.validate(createEventRequest))
            return ResponseEntity.status(HttpStatus.CONFLICT).body(requestValidationService.getMessage())
        return eventService.createEvent(createEventRequest, authPerson.id)
    }

    @GetMapping("/getItemsIn/{eventId}")
    @Operation(summary = "Список всех предметов мероприятия",
               description = "В пагинации указывается только page и size",
               security = [SecurityRequirement(name = "bearer-key")])
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", content = [(Content(mediaType = "application/json", array = (ArraySchema(schema = Schema(implementation = ItemResponseEntity::class)))))]),
        ApiResponse(responseCode = "471", description = "Мероприятия с таким id не существует"),
        ApiResponse(responseCode = "473", description = "Только участник или админ могут получить полезные предметы")
    ])
    fun getItems(@PathVariable@Parameter(description = "Id мероприятия") eventId: Long,
                 @AuthenticationPrincipal authPerson: JwtPerson): ResponseEntity<*> {
        return eventService.getItems(eventId, authPerson)
    }

    @PutMapping("/editItemsIn/{eventId}")
    @Operation(summary = "Редактирование предметов мероприятия", security = [SecurityRequirement(name = "bearer-key")])
    @ApiResponses( value = [
        ApiResponse(responseCode = "200"),
        ApiResponse(responseCode = "409", description = "Некорректный запрос"),
        ApiResponse(responseCode = "471", description = "Мероприятия с таким id не существует")
    ])
    fun editItems(@PathVariable@Parameter(description = "Id мероприятия") eventId: Long,
                  @RequestBody editItemsRequest: List<EditItemsRequestEntity>,
                  @AuthenticationPrincipal authPerson: JwtPerson): ResponseEntity<*> {
        if(!requestValidationService.validate(editItemsRequest))
            return ResponseEntity.status(HttpStatus.CONFLICT).body(requestValidationService.getMessage())
        return eventService.editItems(eventId, editItemsRequest, authPerson.id)
    }

    @PostMapping("/kickPerson")
    @Operation(summary = "Забанить пользователя в мероприятии", security = [SecurityRequirement(name = "bearer-key")])
    @ApiResponses( value = [
        ApiResponse(responseCode = "200"),
        ApiResponse(responseCode = "471", description = "Мероприятия с таким id не существует"),
        ApiResponse(responseCode = "472", description = "Пользователя с таким никнеймом не существует")
    ])
    fun banPerson(@RequestBody kickPersonRequest: KickPersonRequestEntity,
                  @AuthenticationPrincipal authPerson: JwtPerson): ResponseEntity<*> {
        return eventService.banPerson(kickPersonRequest.eventId, kickPersonRequest.userNickname, authPerson)
    }

    @DeleteMapping("/deleteEvent/{eventId}")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200"),
        ApiResponse(responseCode = "471", description = "Мероприятия с таким id не существует")
    ])
    @Operation(summary = "Удаление мероприятия", security = [SecurityRequirement(name = "bearer-key")])
    fun deleteEvent(@PathVariable@Parameter(description = "Id мероприятия") eventId: Long,
                    @AuthenticationPrincipal authPerson: JwtPerson): ResponseEntity<*> {
        return eventService.deleteEvent(eventId, authPerson)
    }

    @PutMapping("/updateEvent")
    @Operation(summary = "Редактирование мероприятия", security = [SecurityRequirement(name = "bearer-key")])
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", content = [(Content(mediaType = "application/json", schema = Schema(implementation = EventResponseEntity::class)))]),
        ApiResponse(responseCode = "409", description = "Некорректный запрос"),
        ApiResponse(responseCode = "471", description = "Мероприятия с таким id не существует")
    ])
    fun updateEvent(@RequestBody request: UpdateEventRequest,
                    @AuthenticationPrincipal authPerson: JwtPerson): ResponseEntity<*> {
        if(!requestValidationService.validate(request))
            return ResponseEntity.status(HttpStatus.CONFLICT).body(requestValidationService.getMessage())
        return eventService.updateEvent(request, authPerson)
    }

    @DeleteMapping("/deleteItemsIn/{eventId}")
    @Operation(summary = "Удаление предметов мероприяитя", security = [SecurityRequirement(name = "bearer-key")])
    @ApiResponses( value = [
        ApiResponse(responseCode = "200"),
        ApiResponse(responseCode = "471", description = "Мероприятия с таким id не существует")
    ])
    fun deleteItems(@PathVariable@Parameter(description = "Id мероприятия") eventId: Long,
                    @AuthenticationPrincipal authPerson: JwtPerson): ResponseEntity<*> {
        return eventService.deleteItems(eventId, authPerson)
    }


    @PutMapping("/markItemIn/{eventId}")
    @Operation(summary = "Отметить предмет", security = [SecurityRequirement(name = "bearer-key")])
    @ApiResponses( value = [
        ApiResponse(responseCode = "200"),
        ApiResponse(responseCode = "471", description = "Мероприятия с таким id не существует"),
        ApiResponse(responseCode = "473", description = "Только участник может отмечать полезные предметы")
    ])
    fun markItems(@PathVariable@Parameter(description = "Id мероприятия") eventId: Long,
                  @RequestBody markItemsRequest: MarkItemRequestEntity,
                  @AuthenticationPrincipal authPerson: JwtPerson): ResponseEntity<*> {
        return eventService.markItem(eventId, markItemsRequest, authPerson)
    }
}