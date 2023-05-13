package com.example.bgm.services

import com.example.bgm.IntegrationEnvironment
import com.example.bgm.controller.dto.CreateEventRequestEntity
import com.example.bgm.entities.Event
import com.example.bgm.entities.Person
import com.example.bgm.entities.enums.Gender
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
class EventServiceTest: IntegrationEnvironment()
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
        assert(responseEvent.id == eventId)
        assert(responseEvent.name == name)
        assert(responseEvent.game == game)
        assert(responseEvent.address == address)
        assert(responseEvent.date == date)
        assert(responseEvent.curPersonCount == 1)
        assert(responseEvent.minAge == null)
        assert(responseEvent.maxAge == null)
        assert(responseEvent.items.isEmpty())

        assertThat(responseEvent.maxAge, `is`(nullValue()))
        assertThat(responseEvent.name, `is`(equalTo(name)))
        assertThat(responseEvent.items, `is`(empty()))
    }

    @Test
    @Rollback
    @Transactional
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
        assert(response.name == name)
        assert(response.game == game)
        assert(response.city == city)
        assert(response.address == address)
        assert(response.date == date)
        assert(response.curPersonCount == 1)
        assert(response.minAge == 18)
        assert(response.maxAge == 25)
        assert(response.description == "gogogo")
        assert(response.hostId == person.id)
    }

}