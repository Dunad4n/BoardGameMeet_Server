package com.example.bgm.services

import com.example.bgm.controller.dto.CreateEventRequestEntity
import com.example.bgm.controller.dto.UpdateEventRequest
import com.example.bgm.entities.Event
import com.example.bgm.entities.Person
import com.example.bgm.entities.enums.Gender
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.repositories.EventRepo
import com.example.bgm.repositories.ItemRepo
import com.example.bgm.repositories.PersonRepo
import com.example.bgm.repositories.RoleRepo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@SpringBootTest
class EventServiceTest
{

    @Autowired
    private lateinit var eventRepo: EventRepo

    @Autowired
    private lateinit var personRepo: PersonRepo

    @Autowired
    private lateinit var itemRepo: ItemRepo

    @Autowired
    private lateinit var roleRepo: RoleRepo

    @Autowired
    private lateinit var eventService: EventService

    @Test
    @Rollback
    @Transactional
    //complete
    fun getEventByIdTest() {
        /** given **/
        val name = "event"
        val game = "game"
        val city = "Voronezh"
        val maxPersonCount = 10
        val address = "address"
        val date = LocalDateTime.now()

        val personName = "Denis"
        val personNickname = "Dunadan"
        val personPassword = "Den221032"
        val secretWord = "secret"
        val gender = Gender.MALE
        val userCity = "Voronezh"

        val person = Person(personName, personNickname, personPassword, secretWord, gender, userCity)
        val event = Event(name, game, city, address, date, maxPersonCount, person)

        event.members.add(person)
        val personId = personRepo.save(person).id
        personRepo.flush()
        val eventId = eventRepo.save(event).id
        eventRepo.flush()

        assert(personId != null)

        /** when **/
        val responseEvent = eventService.getEvent(eventId)

        /** then **/
        assertThat(responseEvent.id, `is`(equalTo(eventId)))
        assertThat(responseEvent.name, `is`(equalTo(name)))
        assertThat(responseEvent.game, `is`(equalTo(game)))
        assertThat(responseEvent.address, `is`(equalTo(address)))
        assertThat(responseEvent.date, `is`(equalTo(date)))
        assertThat(responseEvent.curPersonCount, `is`(equalTo(1)))
        assertThat(responseEvent.minAge, `is`(nullValue()))
        assertThat(responseEvent.maxAge, `is`(nullValue()))
        assertThat(responseEvent.items, `is`(empty()))
    }

    @Test
    @Rollback
    @Transactional
    //complete
    fun createEventTest() {
        /** given **/
        val name = "event"
        val game = "game"
        val city = "Voronezh"
        val maxPersonCount = 10
        val address = "address"
        val date = LocalDateTime.now()
        val minAge = 18
        val maxAge = 25
        val description = "gogogo"

        val personName = "Denis"
        val personNickname = "Dunadan"
        val personPassword = "Den221032"
        val secretWord = "secret"
        val gender = Gender.MALE
        val userCity = "Voronezh"

        val person = personRepo.save(Person(personName, personNickname, personPassword, secretWord, gender, userCity))
        personRepo.flush()
        val createEventRequest = CreateEventRequestEntity(name, game, city, address, date,
                                                            maxPersonCount, minAge, maxAge, description)

        /** when **/
        val response = eventService.createEvent(createEventRequest, person.id)

        /** then **/
//        assert(response.id == eventId)
        assertThat(response.name, `is`(equalTo(name)))
        assertThat(response.game, `is`(equalTo(game)))
        assertThat(response.city, `is`(equalTo(city)))
        assertThat(response.address, `is`(equalTo(address)))
        assertThat(response.date, `is`(equalTo(date)))
        assertThat(response.curPersonCount, `is`(equalTo(1)))
        assertThat(response.minAge, `is`(equalTo(18)))
        assertThat(response.maxAge, `is`(equalTo(25)))
        assertThat(response.description, `is`(equalTo("gogogo")))
        assertThat(response.hostId, `is`(equalTo(person.id)))
    }

    @Test
    @Rollback
    @Transactional
    fun updateEventTest() {
        /** given **/
        val name = "event"
        val game = "game"
        val city = "Voronezh"
        val maxPersonCount = 10
        val address = "address"
        val date = LocalDateTime.now()
        val minAge = 18
        val maxAge = 25
        val description = "gogogo"

        val personName = "Ivan"
        val personNickname = "Vanius"
        val personPassword = "1234"
        val secretWord = "secret"
        val gender = Gender.MALE
        val userCity = "Voronezh"

        val upName = "event1"
        val upGame = "game1"
        val upCity = "Moscow"
        val upMaxPersonCount = 10
        val upAddress = "address"
        val upDate = LocalDateTime.now()
        val upMinAge = 20
        val upMaxAge = 27
        val upDescription = "gogogo1"

        val person = personRepo.save(Person(personName, personNickname, personPassword, secretWord, gender, userCity))
        personRepo.flush()
        val event = eventRepo.save(Event(name, game, city, address, date, maxPersonCount, person))
        eventRepo.flush()

        /** when **/
        eventService.updateEvent(UpdateEventRequest(event.id, upName, upGame, upCity, upAddress, upDate,
            upMaxPersonCount, upMinAge, upMaxAge, upDescription))

        /** then **/
        assertThat(event.name, `is`(equalTo(upName)))
        assertThat(event.game, `is`(equalTo(upGame)))
        assertThat(event.city, `is`(equalTo(upCity)))
        assertThat(event.address, `is`(equalTo(upAddress)))
        assert(event.date.isAfter(date))
        assertThat(event.maxPersonCount, `is`(equalTo(upMaxPersonCount)))
        assertThat(event.minAge, `is`(equalTo(upMinAge)))
        assertThat(event.maxAge, `is`(equalTo(upMaxAge)))
        assertThat(event.description, `is`(equalTo(upDescription)))
    }

    @Test
    @Rollback
    @Transactional
    fun deleteEventTest() {

        val person = JwtPerson(1L, "", "", listOf())
    }

    @Test
    @Rollback
    @Transactional
    //complete
    fun banPersonTest() {
        /** given **/
        val name = "event"
        val game = "game"
        val city = "Voronezh"
        val maxPersonCount = 10
        val address = "address"
        val date = LocalDateTime.now()

        val personName = "Ivan"
        val personNickname = "Vanius"
        val personPassword = "1234"
        val secretWord = "secret"
        val gender = Gender.MALE
        val userCity = "Voronezh"

        val person = personRepo.save(Person(personName, personNickname, personPassword, secretWord, gender, userCity))
        personRepo.flush()
        val event = eventRepo.save(Event(name, game, city, address, date, maxPersonCount, person))
        eventRepo.flush()

        /** when **/
        eventService.banPerson(event.id, person.nickname)

        assertThat(event.bannedMembers[0], `is`(equalTo(person)))
    }
}