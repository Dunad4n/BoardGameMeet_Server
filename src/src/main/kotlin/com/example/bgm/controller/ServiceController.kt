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
        path = ["/events1"],
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
        path = ["/person/create"],
        method = [RequestMethod.POST]
    )
    fun createPerson(@RequestBody createPersonRequest: CreatePersonRequestEntity) {
        personService.createPerson(createPersonRequest)
    }

    @RequestMapping(
        path = ["/profile/{userId}"],
        method = [RequestMethod.GET]
    )
    fun profile(@PathVariable userId: Long): ProfileResponseEntity {
        return personService.getProfile(userId)
    }

    @RequestMapping(
        path = ["/event/create"],
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
        path = ["/event/{eventId}/items/get"],
        method = [RequestMethod.GET]
    )
    fun getItems(@PathVariable eventId: Long): List<String> {
        return eventService.getItems(eventId)
    }

    @RequestMapping(
        path = ["/event/{eventId}/items/edit"],
        method = [RequestMethod.POST]
    )
    fun editItems(@PathVariable eventId: Long, @RequestBody editItemsRequest: EditItemsRequestEntity) {
        return eventService.editItems(eventId, editItemsRequest)
    }

    @RequestMapping(
        path = ["/updateEvent/{eventId}/items/edit"],
        method = [RequestMethod.POST]
    )
    fun updateEvent(@PathVariable eventId: Long, @RequestBody editItemsRequest: EditItemsRequestEntity) {
        return eventService.editItems(eventId, editItemsRequest)
    }

    @RequestMapping(
        path = ["/updnt/"],
        method = [RequestMethod.GET]
    )
    fun testException(): String? {
        throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "aa Not Found"
        )
    }
}