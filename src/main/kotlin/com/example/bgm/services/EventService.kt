package com.example.bgm.services

import com.example.bgm.controller.*
import com.example.bgm.entities.Event
import com.example.bgm.entities.Gender
import com.example.bgm.entities.Person
import com.example.bgm.repositories.EventRepo
import com.example.bgm.repositories.PersonRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class EventService {

    @Autowired
    lateinit var eventRepo: EventRepo

    @Autowired
    lateinit var personRepo: PersonRepo

//    private val persons = listOf(
//        Person("name", "nickname", "password", "secret word", Gender.Male, "City"),
//        Person("name2", "nickname2", "password2", "secret word2", Gender.Male, "City2")
//
//    )
//
//    private val events = listOf(
//        Event("name", "game", "city1", "address1", LocalDateTime.now(), 12, ),
//        Event("name", "game", "city2", "address2", LocalDateTime.now(), 12, 18, 25)
//    )

    private fun mapToEventsResponseEntity(event: Event): EventsResponseEntity{
        return EventsResponseEntity(event.name,
                                    event.game,
                                    event.address,
                                    event.date,
                                    event.members.size,
                                    event.maxPersonCount,
                                    event.minAge,
                                    event.maxAge)
    }

    private fun mapToMyEventsResponseEntity(event: Event, user: Person): MyEventsResponseEntity {
        return MyEventsResponseEntity(event.name,
                                        event.game,
                                        event.address,
                                        event.date,
                                        event.members.size,
                                        event.maxPersonCount,
                                        event.minAge,
                                        event.maxAge,
                                   event.host == user)
    }

    fun getEvent(id: Long): Event? {
        return eventRepo.findById(id).get()
    }

    fun createEvent(createEventRequest: CreateEventRequestEntity) {
        val host = personRepo.findById(createEventRequest.hostId).get()
        val event = Event(createEventRequest.name,
                          createEventRequest.game,
                          createEventRequest.city,
                          createEventRequest.address,
                          createEventRequest.date,
                          createEventRequest.maxPersonCount,
                          host,
                          createEventRequest.minAge,
                          createEventRequest.maxAge,
                          createEventRequest.description)
        eventRepo.save(event)
    }

    /**
     * обсудить реквесты с клиента
     */
    fun updateEvent(updateRequest: UpdateEventRequestEntity) {
        val event = eventRepo.findById(updateRequest.id).get()
        event.name = updateRequest.name
        event.game = updateRequest.game
        event.city = updateRequest.city
        event.address = updateRequest.address
        event.date = updateRequest.date
        event.maxPersonCount = updateRequest.maxPersonCount
        event.maxAge = updateRequest.maxAge
        event.minAge = updateRequest.minAge
        event.description = updateRequest.name
        eventRepo.save(event)
    }

    fun deleteEvent(id: Long) {
        eventRepo.deleteById(id)
    }

    // поменять events на запрос к бд
    fun getMainPageEvents(userId: Long, start: Int, search: String = ""): ArrayList<EventsResponseEntity>? {
        val events = if (search != "") {
            eventRepo.findAllByCityAndName(personRepo.findById(userId).get().city, search)
        } else {
            eventRepo.findAllByCity(personRepo.findById(userId).get().city)
        }
        val res = arrayListOf<EventsResponseEntity>()
        if (events != null) {
            for(event in sortEventsForMainPage(filterEvents(events))) {
                res.add(mapToEventsResponseEntity(event))
            }
        } else {
            return null
        }
        return res
    }

//    // поменять events на запрос к бд
//    fun getEventsWithSearch(user: Int, start: Int, search: String): ArrayList<EventsResponseEntity> {
//        val res: ArrayList<EventsResponseEntity> = arrayListOf()
//        for(event in events)
//            res.add(mapToEventsResponseEntity(event))
//        return res
//    }

    // поменять events и  persons на запрос к бд
    fun getMyEventsPageEvent(userId: Long, start: Int): ArrayList<MyEventsResponseEntity> {
        val user = personRepo.findById(userId).get()
        val myEvents = user.events
        val res = arrayListOf<MyEventsResponseEntity>()
        for(event in sortEventsForMyEventPage(myEvents)) {
            res.add(mapToMyEventsResponseEntity(event, user))
        }
        return res
    }

    fun banPerson(eventId: Long, userNickname: String) {
        val user = personRepo.findByNickname(userNickname)
        if (user != null) {
            eventRepo.findById(eventId).get().ban(user)
        }
    }

    fun getItems(id: Long): List<String> {
        val event = eventRepo.findById(id).get()
        return event.items.split(event.getSpace())
    }

    fun editItems(id: Long, editItemsRequest: EditItemsRequestEntity) {
        val event = eventRepo.findById(id).get()
        val items = editItemsRequest.items
        event.items = ""
        for (i in 0..items.size - 2) {
            event.items += items[i] + event.getSpace()
        }
        event.items += items[items.size - 1]
        eventRepo.save(event)
    }

    private fun sortEventsForMainPage(events: List<Event>): List<Event> {
        return events.sortedWith(compareBy({ -it.date.year }, { -it.date.dayOfYear }, { it.membersForFull() }))
    }

    private fun sortEventsForMyEventPage(events: List<Event>): List<Event> {
        val activeEvents = mutableListOf<Event>()
        val inactiveEvents = mutableListOf<Event>()
        val resultEvents = mutableListOf<Event>()
        for (event in events) {
            if (event.isActive()) {
                activeEvents.add(event)
            } else {
                inactiveEvents.add(event)
            }
        }
        resultEvents.addAll(activeEvents.sortedWith(compareBy { it.date.toEpochSecond(ZoneOffset.UTC) }))
        resultEvents.addAll(inactiveEvents.sortedWith(compareBy { -it.date.toEpochSecond(ZoneOffset.UTC) }))
        return resultEvents
    }

    private fun filterEvents(events: List<Event>): List<Event> {
        return events.filter { it.isActive() }
    }
}