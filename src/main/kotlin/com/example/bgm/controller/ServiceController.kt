package com.example.bgm.controller

import com.example.bgm.services.EventService
import com.example.bgm.services.PersonService
import com.example.bgm.services.RequestValidationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException


@RestController
class ServiceController{

    @Autowired
    private lateinit var eventService: EventService

    @Autowired
    private lateinit var personService: PersonService

    @Autowired
    private lateinit var requestValidationService: RequestValidationService

    @RequestMapping(
        path = ["/events"],
        method = [RequestMethod.GET]
    )
    fun allEvents(@RequestParam(value = "id") id: Long,
                     @RequestParam(value = "received") received: Int): List<MainPageEventResponseEntity> {
        return eventService.getMainPageEvents(id, received)
    }

    @RequestMapping(
        path = ["/eventsWithSearch"],
        method = [RequestMethod.GET]
    )
    fun eventsWithSearch(@RequestParam(value = "id") id: Long,
                            @RequestParam(value = "received") received: Int,
                            @RequestParam(value = "search") search: String): List<MainPageEventResponseEntity> {
        return eventService.getMainPageEvents(id, received, search)
    }

    @RequestMapping(
        path = ["/myEvents"],
        method = [RequestMethod.GET]
    )
    fun myEvents(@RequestParam(value = "id") id: Long,
                     @RequestParam(value = "received") received: Int): List<MyEventsResponseEntity> {
        return eventService.getMyEventsPageEvent(id, received)
    }

    @RequestMapping(
        path = ["/event"],
        method = [RequestMethod.GET]
    )
    fun event(@RequestParam(value = "id") id: Long): EventResponseEntity {
        return eventService.getEvent(id)
    }

    @RequestMapping(
        path = ["/createPerson"],
        method = [RequestMethod.POST]
    )
    fun createPerson(@RequestBody createPersonRequest: CreatePersonRequestEntity) {
        if(!requestValidationService.validate(createPersonRequest))
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST, requestValidationService.getMessage()
            )
        personService.createPerson(createPersonRequest)
    }

    @RequestMapping(
        path = ["/profile"],
        method = [RequestMethod.GET]
    )
    fun profile(@RequestParam(value = "id") userId: Long): ProfileResponseEntity {
        return personService.getProfile(userId)
    }

    @RequestMapping(
        path = ["/createEvent"],
        method = [RequestMethod.POST]
    )
    fun createEvent(@RequestBody createEventRequest: CreateEventRequestEntity) {
        if(!requestValidationService.validate(createEventRequest))
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST, requestValidationService.getMessage()
            )
        eventService.createEvent(createEventRequest)
    }

    @RequestMapping(
        path = ["/getItems"],
        method = [RequestMethod.GET]
    )
    fun getItems(@RequestParam(value = "id") eventId: Long): List<String> {
        return eventService.getItems(eventId)
    }

    @RequestMapping(
        path = ["/editItems"],
        method = [RequestMethod.POST]
    )
    fun editItems(@RequestBody eventId: Long, @RequestBody editItemsRequest: EditItemsRequestEntity) {
        if(!requestValidationService.validate(editItemsRequest))
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST, requestValidationService.getMessage()
            )
        return eventService.editItems(eventId, editItemsRequest)
    }

    @RequestMapping(
        path = ["/leaveEvent"],
        method = [RequestMethod.POST]
    )
    fun leaveEvent(@RequestBody eventId: Long, @RequestBody userId: Long) {
        val user = personService.getPerson(userId)
            ?: throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "UNAUTHORIZED"
            )
        personService.leaveFromEvent(user, eventId)
    }

    @RequestMapping(
        path = ["/joinEvent"],
        method = [RequestMethod.POST]
    )
    fun joinEvent(@RequestBody eventId: Long, @RequestBody userId: Long) {
        val user = personService.getPerson(userId)
            ?: throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "UNAUTHORIZED"
            )
        personService.joinToEvent(user, eventId)
    }

    @RequestMapping(
        path = ["/getAllMembers"],
        method = [RequestMethod.GET]
    )
    fun getAllMembers(@RequestParam(value = "id") eventId: Long): ArrayList<MemberResponseEntity> {
        return personService.getAllMembers(eventId)
    }

    @RequestMapping(
        path = ["/deletePerson"],
        method = [RequestMethod.POST]
    )
    fun deletePerson(@RequestBody userId: Long) {
        personService.deletePerson(userId)
    }

    @RequestMapping(
        path = ["/updatePerson"],
        method = [RequestMethod.POST]
    )
    fun updatePerson(@RequestBody request: UpdatePersonRequestEntity) {
        if(!requestValidationService.validate(request))
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST, requestValidationService.getMessage()
            )
        personService.updatePerson(request)
    }

    @RequestMapping(
        path = ["/banPerson"],
        method = [RequestMethod.POST]
    )
    fun banPerson(@RequestBody eventId: Long, @RequestBody userNickname: String) {
        eventService.banPerson(eventId, userNickname)
    }

    @RequestMapping(
        path = ["/deleteEvent"],
        method = [RequestMethod.POST]
    )
    fun deleteEvent(@RequestBody eventId: Long) {
        eventService.deleteEvent(eventId)
    }

    @RequestMapping(
        path = ["/updateEvent"],
        method = [RequestMethod.POST]
    )
    fun updateEvent(@RequestBody request: UpdateEventRequestEntity) {
        if(!requestValidationService.validate(request))
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST, requestValidationService.getMessage()
            )
        eventService.updateEvent(request)
    }
}