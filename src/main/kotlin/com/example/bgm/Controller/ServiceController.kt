package com.example.bgm.Controller

import com.example.bgm.TestService
import com.example.bgm.entities.Event
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ServiceController {
    val service: TestService = TestService();

    @RequestMapping(
        path = ["/events"],
        method = [RequestMethod.GET]
    )
    fun allEvents(@RequestParam(value = "id") id: Int,
                     @RequestParam(value = "received") received: Int): List<EventsResponseEntity> {
        return service.getEvents(id, received)
    }
//объединить в 1 с оптионал прараметром в сервисе тоже
    @RequestMapping(
        path = ["/events1"],
        method = [RequestMethod.GET]
    )
    fun eventsWithSearch(@RequestParam(value = "id") id: Int,
                            @RequestParam(value = "received") received: Int,
                            @RequestParam(value = "search") search: String): List<EventsResponseEntity> {
        return service.getEventsWithSearch(id, received, search)
    }

    @RequestMapping(
        path = ["/myEvents"],
        method = [RequestMethod.GET]
    )
    fun myEvents(@RequestParam(value = "id") id: Int,
                     @RequestParam(value = "received") received: Int): List<MyEventsResponseEntity> {
        return service.getMyEvents(id, received)
    }

    @RequestMapping(
        path = ["/event"],
        method = [RequestMethod.GET]
    )
    fun event(@RequestParam(value = "id") id: Int): Event {
        return service.getEvent(id)
    }

    @RequestMapping(
        path = ["/profile"],
        method = [RequestMethod.GET]
    )
    fun profile(@RequestParam(value = "id") id: Int): ProfileResponseEntity {
        return service.getProfile(id)
    }
}