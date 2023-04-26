package com.example.bgm

import com.example.bgm.Controller.EventsResponseEntity
import com.example.bgm.Controller.MyEventsResponseEntity
import com.example.bgm.Controller.ProfileResponseEntity
import com.example.bgm.entities.Event
import com.example.bgm.entities.Gender
import com.example.bgm.entities.Person
import java.sql.Date
import java.time.LocalDate

class TestService {

    private val persons = listOf(
        Person("name", "nickname", "password", "secret word", Gender.Male, 20, "City", listOf(), listOf()),
        Person("name2", "nickname2", "password2", "secret word2", Gender.Male, 20, "City2", listOf(), listOf())

    )

    private val events = listOf(
        Event("name", "game", "address", Date(12313), 12, 18, 25, persons[0], persons, persons, "description", "items", listOf(), true),
        Event("name", "game", "address", Date(12313), 12, 18, 25, persons[0], persons, persons, "description", "items", listOf(), true)
    )

    private fun mapToEventsResponseEntity(event: Event): EventsResponseEntity{
        return EventsResponseEntity(event.name,
                                    event.game,
                                    event.address,
                                    event.date,
                                    event.people.size,
                                    event.maxPersonCount,
                                    event.minAge,
                                    event.maxAge)
    }

    private fun mapToMyEventsResponseEntity(event: Event, user: Person): MyEventsResponseEntity {
        return MyEventsResponseEntity(event.name,
            event.game,
            event.address,
            event.date,
            event.people.size,
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
    fun getEvents(user: Int, start: Int): ArrayList<EventsResponseEntity> {
        val res: ArrayList<EventsResponseEntity> = arrayListOf()
        for(event in events)
            res.add(mapToEventsResponseEntity(event))
        return res
    }

    // поменять events на запрос к бд
    fun getEventsWithSearch(user: Int, start: Int, search: String): ArrayList<EventsResponseEntity> {
        val res: ArrayList<EventsResponseEntity> = arrayListOf()
        for(event in events)
            res.add(mapToEventsResponseEntity(event))
        return res
    }

    // поменять events и  persons на запрос к бд
    fun getMyEvents(user: Int, start: Int): ArrayList<MyEventsResponseEntity> {
        val res: ArrayList<MyEventsResponseEntity> = arrayListOf()
        for(event in events)
            res.add(mapToMyEventsResponseEntity(event, persons[0]))
        return res
    }

    // поменять persons на запрос к бд
    fun getProfile(user: Int): ProfileResponseEntity {
        return mapToProfileResponseEntity(persons[0])
    }

    fun getEvent(i: Int) = events[i]
}