package com.example.bgm.controller

import com.example.bgm.services.EventService
import com.example.bgm.entities.Event
import com.example.bgm.services.PersonService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
class ServiceController{

    @Autowired
    private lateinit var eventService: EventService

    @Autowired
    private lateinit var personService: PersonService

    @RequestMapping(
        path = ["/events"],
        method = [RequestMethod.GET]
    )
    fun allEvents(@RequestParam(value = "id") id: Long,
                     @RequestParam(value = "received") received: Int): List<EventsResponseEntity>? {
        return eventService.getMainPageEvents(id, received)
    }
//объединить в 1 с оптионал прараметром в сервисе тоже
    @RequestMapping(
        path = ["/events1"],
        method = [RequestMethod.GET]
    )
    fun eventsWithSearch(@RequestParam(value = "id") id: Long,
                            @RequestParam(value = "received") received: Int,
                            @RequestParam(value = "search") search: String): List<EventsResponseEntity>? {
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
    fun event(@RequestParam(value = "id") id: Long): Event? {
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
        path = ["/profile/{id}"],
        method = [RequestMethod.GET]
    )
    fun profile(@PathVariable id: Long): ProfileResponseEntity {
        return personService.getProfile(id)
    }
}