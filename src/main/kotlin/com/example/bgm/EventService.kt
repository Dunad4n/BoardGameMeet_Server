package com.example.bgm

import com.example.bgm.Controller.EventsResponseEntity
import com.example.bgm.Controller.MyEventsResponseEntity
import com.example.bgm.Controller.ProfileResponseEntity
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
    val eventRepo: EventRepo? = null

    @Autowired
    val personRepo: PersonRepo? = null

    private val persons = listOf(
        Person("name", "nickname", "password", "secret word", Gender.Male, 20, "City", 5, listOf(), listOf()),
        Person("name2", "nickname2", "password2", "secret word2", Gender.Male, 20, "City2", 10, listOf(), listOf())

    )

    private val events = listOf(
        Event("name", "game", "city1", "address1", LocalDateTime.now(), 12, 18, 25, persons[0], persons, persons, "description", "items", listOf()),
        Event("name", "game", "city2", "address2", LocalDateTime.now(), 12, 18, 25, persons[0], persons, persons, "description", "items", listOf())
    )

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

    private fun mapToProfileResponseEntity(person: Person): ProfileResponseEntity {
        return ProfileResponseEntity(person.name,
            person.nickname,
            person.age,
            person.city,
            "avatar",
            person.gender)
    }

    // поменять events на запрос к бд
    fun getEvents(userId: Long, start: Int, search: String = ""): ArrayList<EventsResponseEntity> {
        val events: List<Event>? = if (search != "") {
            val user = personRepo?.findById(userId)?.get()
            user?.city?.let {
                city -> user.name.let {
                    name -> eventRepo?.findAllByCityAndName(city, name)
                }
            }
        } else {
            personRepo?.findById(userId)?.get()?.city?.let {
                city -> eventRepo?.findAllByCity(city)
            }
        }
        val res = arrayListOf<EventsResponseEntity>()
        if (events != null) {
            for(event in sortEventsForMainPage(filterEvents(events))) {
                res.add(mapToEventsResponseEntity(event))
            }
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
    fun getMyEvents(userId: Long, start: Int): ArrayList<MyEventsResponseEntity> {
        val user = personRepo?.findById(userId)?.get()
        val myEvents = user?.events
        val res = arrayListOf<MyEventsResponseEntity>()
        if (myEvents != null) {
            for(event in sortEventsForMyEventPage(myEvents)) {
                res.add(mapToMyEventsResponseEntity(event, user))
            }
        } else {
            throw Exception("my events or user is null")
        }
        return res
    }

    // поменять persons на запрос к бд
    fun getProfile(user: Int): ProfileResponseEntity {
        return mapToProfileResponseEntity(persons[0])
    }

    fun getEvent(i: Int) = events[i]

    fun sortEventsForMainPage(events: List<Event>): List<Event> {
        return events.sortedWith(compareBy({ -it.date.year }, { -it.date.dayOfYear }, { it.membersForFull() }))
    }

    fun sortEventsForMyEventPage(events: List<Event>): List<Event> {
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

    fun filterEvents(events: List<Event>): List<Event> {
        return events.filter { it.isActive() }
    }
}