package com.example.bgm.controller

import com.example.bgm.jwt.JwtPerson
import com.example.bgm.services.EventService
import com.example.bgm.services.PersonService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.annotation.AuthenticationPrincipal
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
                     @RequestParam(value = "received") received: Int): List<MainPageEventResponseEntity>? {
        return eventService.getMainPageEvents(id, received)
    }
//объединить в 1 с оптионал прараметром в сервисе тоже
    @RequestMapping(
        path = ["/events1"],
        method = [RequestMethod.GET]
    )
    fun eventsWithSearch(@RequestParam(value = "id") id: Long,
                            @RequestParam(value = "received") received: Int,
                            @RequestParam(value = "search") search: String): List<MainPageEventResponseEntity>? {
        return eventService.getMainPageEvents(id, received, search)
    }

    @RequestMapping(
        path = ["/myEvents"],
        method = [RequestMethod.GET]
    )
    fun myEvents(@AuthenticationPrincipal authPerson: JwtPerson,
                     @RequestParam(value = "received") received: Int): List<MyEventsResponseEntity> {
        if (authPerson.id == null) {
            throw Exception("person id is null")
        }
        return eventService.getMyEventsPageEvent(authPerson.id, received)
    }

    @RequestMapping(
        path = ["/event"],
        method = [RequestMethod.GET]
    )
    fun event(@RequestParam(value = "id") id: Long): EventResponseEntity {
        return eventService.getEvent(id)
    }

//    @RequestMapping(
//        path = ["/person/create"],
//        method = [RequestMethod.POST]
//    )
//    fun createPerson(@RequestBody createPersonRequest: CreatePersonRequestEntity) {
//        personService.createPerson(createPersonRequest)
//    }

    @RequestMapping(
        path = ["/profile/{nickname}"],
        method = [RequestMethod.GET]
    )
    fun profile(@PathVariable nickname: String, @AuthenticationPrincipal authPerson: JwtPerson): ProfileResponseEntity {
        return personService.getProfile(nickname)
    }

    @RequestMapping(
        path = ["/event/create"],
        method = [RequestMethod.POST]
    )
    fun createEvent(@RequestBody createEventRequest: CreateEventRequestEntity,
                    @AuthenticationPrincipal authPerson: JwtPerson) {
        eventService.createEvent(createEventRequest, authPerson.id)
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
    fun editItems(@PathVariable eventId: Long,
                  @RequestBody editItemsRequest: EditItemsRequestEntity,
                  @AuthenticationPrincipal authPerson: JwtPerson) {
        return eventService.editItems(eventId, editItemsRequest, authPerson)
    }

    @RequestMapping(
        path = ["/joinToEvent"],
        method = [RequestMethod.PUT]
    )
    fun jointToEvent(@RequestBody joinRequest: JoinOrLeaveEventRequestEntity,
                     @AuthenticationPrincipal authPerson: JwtPerson) {
        if (authPerson.id == null) {
           throw Exception("person id is null")
        }
        return personService.joinToEvent(authPerson.id, joinRequest.eventId)
    }

    @RequestMapping(
        path = ["/event/{eventId}/members"],
        method = [RequestMethod.GET]
    )
    fun getAllMembers(@PathVariable eventId: Long): ArrayList<MemberResponseEntity> {
        return personService.getAllMembers(eventId)
    }

    @RequestMapping(
        path = ["/leaveFromEvent"],
        method = [RequestMethod.PUT]
    )
    fun leaveFromEvent(@RequestBody leaveRequest: JoinOrLeaveEventRequestEntity,
                       @AuthenticationPrincipal authPerson: JwtPerson) {
        if (authPerson.id == null) {
            throw Exception("person id is null")
        }
        return personService.leaveFromEvent(authPerson.id, leaveRequest.eventId)
    }
}