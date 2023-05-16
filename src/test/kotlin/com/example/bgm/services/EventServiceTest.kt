package com.example.bgm.services

import com.example.bgm.IntegrationEnvironment
import com.example.bgm.controller.dto.CreateEventRequestEntity
import com.example.bgm.controller.dto.UpdateEventRequest
import com.example.bgm.entities.Event
import com.example.bgm.entities.Item
import com.example.bgm.entities.Person
import com.example.bgm.entities.enums.Gender
import com.example.bgm.repositories.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.shaded.org.hamcrest.Matchers.hasSize
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
    private lateinit var messageRepo: MessageRepo

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

        val upName = "event"
        val upGame = "game"
        val upCity = "Voronezh"
        val upMaxPersonCount = 10
        val upAddress = "address"
        val upDate = LocalDateTime.now()
        val upMinAge = 18
        val upMaxAge = 25
        val upDescription = "gogogo"

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
        eventService.updateEvent(UpdateEventRequest(event.id, upName, upGame, upCity, upAddress, upDate,
            upMaxPersonCount, upMinAge, upMaxAge, upDescription))
        val updateEvent = eventRepo.findById(event.id).get()

        /** then **/

        assertThat(updateEvent.name, `is`(equalTo(upName)))
        assertThat(updateEvent.game, `is`(equalTo(upGame)))
        assertThat(updateEvent.city, `is`(equalTo(upCity)))
        assertThat(updateEvent.maxPersonCount, `is`(equalTo(upMaxPersonCount)))
        assertThat(updateEvent.address, `is`(equalTo(upAddress)))
        assert(updateEvent.date.isAfter(date))
        assertThat(updateEvent.minAge, `is`(equalTo(upMinAge)))
        assertThat(updateEvent.maxAge, `is`(equalTo(upMaxAge)))
        assertThat(updateEvent.description, `is`(equalTo(upDescription)))

    }

    @Test
    @Rollback
    @Transactional
    fun createMessageTest() {
        /** given **/
        val text = "text"
        val date = LocalDateTime.now()


        /** when **/
        //val response = eventService.createEvent(createEventRequest, person.id)

        /** then **/
        //assert(response.name == name)
        //assert(response.game == game)
    }

    @Test
    @Rollback
    @Transactional
    fun deleteEventTest() {
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

        /** when **/
        //val response = eventService.createEvent(createEventRequest, person.id)

        /** then **/
        //assert(response.name == name)
        //assert(response.game == game)
    }

    @Test
    @Rollback
    @Transactional
    fun getMainPageEventsTest() {
        /** given **/

        /** when **/
        //val response = eventService.createEvent(createEventRequest, person.id)

        /** then **/
        //assert(response.name == name)
        //assert(response.game == game)
    }

    @Test
    @Rollback
    @Transactional
    fun getMyEventsPageEventTest() {
        /** given **/

        /** when **/
        //val response = eventService.createEvent(createEventRequest, person.id)

        /** then **/
        //assert(response.name == name)
        //assert(response.game == game)
    }

    @Test
    @Rollback
    @Transactional
    fun banPersonTest() {
        /** given **/

        /** when **/
        //val response = eventService.createEvent(createEventRequest, person.id)

        /** then **/
        //assert(response.name == name)
        //assert(response.game == game)
    }

    @Test
    @Rollback
    @Transactional
    fun getItemsTest() {
        /** given **/

        /** when **/
        //val response = eventService.createEvent(createEventRequest, person.id)

        /** then **/
        //assert(response.name == name)
        //assert(response.game == game)
    }

    @Test
    @Rollback
    @Transactional
    fun editItemsTest() {
        /** given **/

        /** when **/
        //val response = eventService.createEvent(createEventRequest, person.id)

        /** then **/
        //assert(response.name == name)
        //assert(response.game == game)
    }

    @Test
    @Rollback
    @Transactional
    fun markItemsTest() {
        /** given **/

        /** when **/
        //val response = eventService.createEvent(createEventRequest, person.id)

        /** then **/
        //assert(response.name == name)
        //assert(response.game == game)
    }

    @Test
    @Rollback
    @Transactional
    fun sortEventsForMainPageTest() {
        /** given **/

        /** when **/
        //val response = eventService.createEvent(createEventRequest, person.id)

        /** then **/
        //assert(response.name == name)
        //assert(response.game == game)
    }

    @Test
    @Rollback
    @Transactional
    fun sortEventsForMyEventPageTest() {
        /** given **/

        /** when **/
        //val response = eventService.createEvent(createEventRequest, person.id)

        /** then **/
        //assert(response.name == name)
        //assert(response.game == game)
    }

    // TODO: filters?

    @Test
    @Rollback
    @Transactional
    fun getMessagesTest() {
        /** given **/

        /** when **/
        //val response = eventService.createEvent(createEventRequest, person.id)

        /** then **/
        //assert(response.name == name)
        //assert(response.game == game)
    }



    @Test
    @Rollback
    @Transactional
    fun getPersonTest() {
        /** given **/

        /** when **/
        //val response = eventService.createEvent(createEventRequest, person.id)

        /** then **/
        //assert(response.name == name)
        //assert(response.game == game)
    }

    @Test
    @Rollback
    @Transactional
    fun updatePersonTest() {
        /** given **/

        /** when **/
        //val response = eventService.createEvent(createEventRequest, person.id)

        /** then **/
        //assert(response.name == name)
        //assert(response.game == game)
    }

    @Test
    @Rollback
    @Transactional
    fun deletePersonTest() {
        /** given **/

        /** when **/
        //val response = eventService.createEvent(createEventRequest, person.id)

        /** then **/
        //assert(response.name == name)
        //assert(response.game == game)
    }

    @Test
    @Rollback
    @Transactional
    fun getAllMembersTest() {
        /** given **/

        /** when **/
        //val response = eventService.createEvent(createEventRequest, person.id)

        /** then **/
        //assert(response.name == name)
        //assert(response.game == game)
    }

    @Test
    @Rollback
    @Transactional
    fun getProfileTest() {
        /** given **/

        /** when **/
        //val response = eventService.createEvent(createEventRequest, person.id)

        /** then **/
        //assert(response.name == name)
        //assert(response.game == game)
    }

    @Test
    @Rollback
    @Transactional
    fun getByNicknameTest() {
        /** given **/

        /** when **/
        //val response = eventService.createEvent(createEventRequest, person.id)

        /** then **/
        //assert(response.name == name)
        //assert(response.game == game)
    }

    @Test
    @Rollback
    @Transactional
    fun joinToEventTest() {
        /** given **/

        /** when **/
        //val response = eventService.createEvent(createEventRequest, person.id)

        /** then **/
        //assert(response.name == name)
        //assert(response.game == game)
    }

    @Test
    @Rollback
    @Transactional
    fun leaveFromEventTest() {
        /** given **/

        /** when **/
        //val response = eventService.createEvent(createEventRequest, person.id)

        /** then **/
        //assert(response.name == name)
        //assert(response.game == game)
    }

    @Test
    @Rollback
    @Transactional
    fun validateSecretWordTest() {
        /** given **/

        /** when **/
        //val response = eventService.createEvent(createEventRequest, person.id)

        /** then **/
        //assert(response.name == name)
        //assert(response.game == game)
    }

    @Test
    @Rollback
    @Transactional
    fun changePasswordTest() {
        /** given **/

        /** when **/
        //val response = eventService.createEvent(createEventRequest, person.id)

        /** then **/
        //assert(response.name == name)
        //assert(response.game == game)
    }

    //validate?
}