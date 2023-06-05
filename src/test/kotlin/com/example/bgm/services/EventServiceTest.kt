package com.example.bgm.services

import com.example.bgm.controller.dto.*
import com.example.bgm.entities.Event
import com.example.bgm.entities.Item
import com.example.bgm.entities.Person
import com.example.bgm.entities.enums.Gender
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.repositories.EventRepo
import com.example.bgm.repositories.ItemRepo
import com.example.bgm.repositories.PersonRepo
import com.example.bgm.repositories.RoleRepo
import org.apache.coyote.Response
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.stream.Collectors

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
        val authPerson = JwtPerson(person.id, personNickname, personPassword, listOf())
        event.members.add(person)

        /** when **/
        val responseEvent = eventService.getEvent(event.id, authPerson)

        /** then **/
        assertThat(responseEvent.id, `is`(equalTo(event.id)))
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
        assertThat(response.statusCode, `is`(equalTo(HttpStatusCode.valueOf(200))))
    }

    @Test
    @Rollback
    @Transactional
    //complete
    fun updateEventTest() {
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
        val authPerson = JwtPerson(person.id, "Vanius", "1234", listOf())


        /** when **/
        val response = eventService.updateEvent(UpdateEventRequest(event.id, upName, upGame, upCity, upAddress, upDate,
            upMaxPersonCount, upMinAge, upMaxAge, upDescription), authPerson)

        /** then **/
        assertThat(response.statusCode, `is`(equalTo(HttpStatusCode.valueOf(200))))
    }

    @Test
    @Rollback
    @Transactional
    //complete
    fun deleteEventTest() {
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


        val authPerson = JwtPerson(person.id, "Vanius", "1234", listOf())

        eventService.deleteEvent(event.id, authPerson)

        assertThat(eventRepo.existsById(event.id), `is`(equalTo(false)))
    }

    @Test
    @Rollback
    @Transactional
    // complete
    fun getMainPageEventsTest() {
        /** given **/
        val name = "event"
        val game = "game"
        val city = "Voronezh"
        val maxPersonCount = 10
        val curPersonCount = 0
        val minAge = 15
        val maxAge = 20
        val description = "gogogo"
        val address = "address"
        val date = LocalDateTime.now()

        val name1 = "event1"
        val game1 = "game1"
        val city1 = "Voronezh"
        val maxPersonCount1 = 10
        val curPersonCount1 = 0
        val address1 = "address1"
        val date1 = LocalDateTime.now()

        val personName = "Ivan"
        val personNickname = "Vanius"
        val personPassword = "1234"
        val secretWord = "secret"
        val age = 18
        val gender = Gender.MALE
        val userCity = "Voronezh"

        val personName1 = "Ivan1"
        val personNickname1 = "Vanius1"
        val personPassword1 = "12341"
        val secretWord1 = "secret1"
        val age1 = 19
        val gender1 = Gender.MALE
        val userCity1 = "Voronezh"

        var person = Person(personName, personNickname, personPassword, secretWord, gender, userCity)
        person.age = 18
        var person1 = Person(personName1, personNickname1, personPassword1, secretWord1, gender1, userCity1)
        person1.age = 19

        person = personRepo.save(person)
        personRepo.flush()
        person1 = personRepo.save(person1)
        personRepo.flush()
        val event = eventRepo.save(Event(name, game, city, address, date, maxPersonCount, person, minAge, maxAge, description))
        eventRepo.flush()
        val event1 = eventRepo.save(Event(name1, game1, city1, address1, date1, maxPersonCount1, person1, minAge, maxAge, description))
        eventRepo.flush()
        val authPerson = JwtPerson(person.id, "Vanius", "1234", listOf())
        val p = PageRequest.of(0, 20)


        val respEvent = MainPageEventResponseEntity(event.id, name, game, address, date, curPersonCount, maxPersonCount, minAge, maxAge, description)
        val respEvent1 = MainPageEventResponseEntity(event1.id, name1, game1, address1, date1, curPersonCount1, maxPersonCount1, minAge, maxAge, description)

        val listEvents = listOf<MainPageEventResponseEntity>(respEvent, respEvent1)

        val res = eventService.getMainPageEvents(person.city,null, p, authPerson)

        assertThat(listEvents[0], `is`(equalTo(res[0])))
        assertThat(listEvents[1], `is`(equalTo(res[1])))

    }

    @Test
    @Rollback
    @Transactional
    //complete
    fun getMyEventsPageEventTest() {
        /** given **/
        val name = "event"
        val game = "game"
        val city = "Voronezh"
        val maxPersonCount = 10
        val curPersonCount = 0
        val address = "address"
        val date = LocalDateTime.now()
        val minAge = 18
        val maxAge = 25

        val name1 = "event1"
        val game1 = "game1"
        val city1 = "Voronezh"
        val maxPersonCount1 = 10
        val curPersonCount1 = 0
        val address1 = "address1"
        val date1 = LocalDateTime.now()
        val minAge1 = 18
        val maxAge1 = 25

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
        val event1 = eventRepo.save(Event(name1, game1, city1, address1, date1, maxPersonCount1, person))
        eventRepo.flush()
        val authPerson = JwtPerson(person.id, "Vanius", "1234", listOf())
        person.events.add(event)
        person.events.add(event1)
        val respEvent = MyEventsResponseEntity(event.id, name, game, address, date, curPersonCount, maxPersonCount,
            minAge, maxAge, null, true)
        val respEvent1 = MyEventsResponseEntity(event1.id, name1, game1, address1, date1, curPersonCount1, maxPersonCount1,
            minAge1, maxAge1, null, true)
        val p = PageRequest.of(0, 20)

        val listEvents = arrayListOf<MyEventsResponseEntity>(respEvent, respEvent1)

        val res = eventService.getMyEventsPageEvent(authPerson, p)

        assertThat(listEvents[0].name, `is`(equalTo(res[0].name)))
        assertThat(listEvents[1].name, `is`(equalTo(res[1].name)))
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

        val personName1 = "Denis"
        val personNickname1 = "Dunadan"
        val personPassword1 = "1234"
        val secretWord1 = "secret"
        val gender1 = Gender.MALE
        val userCity1 = "Voronezh"

        val person = personRepo.save(Person(personName, personNickname, personPassword, secretWord, gender, userCity))
        personRepo.flush()
        val person1 = personRepo.save(Person(personName1, personNickname1, personPassword1, secretWord1, gender1, userCity1))
        personRepo.flush()
        val event = eventRepo.save(Event(name, game, city, address, date, maxPersonCount, person))
        eventRepo.flush()
        event.members.add(person1)
        val authPerson = JwtPerson(person.id, personNickname, personPassword, listOf())

        /** when **/
        eventService.banPerson(event.id, person1.nickname, authPerson)

        assertThat(event.bannedMembers[0], `is`(equalTo(person1)))
    }

    @Test
    @Rollback
    @Transactional
    //complete
    fun getItemsTest() {
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

        val item1Name = "item1"
        val item2Name = "item2"
        val item1Marked = true
        val item2Marked = false

        val person = personRepo.save(Person(personName, personNickname, personPassword, secretWord, gender, userCity))
        personRepo.flush()
        val event = eventRepo.save(Event(name, game, city, address, date, maxPersonCount, person))
        eventRepo.flush()
        val item1 = itemRepo.save(Item(item1Name, item1Marked))
        item1.event = event
        itemRepo.flush()
        val item2 = itemRepo.save(Item(item2Name, item2Marked))
        item2.event = event
        itemRepo.flush()
        val authPerson = JwtPerson(person.id, personNickname, personPassword, listOf())
        event.members.add(person)
        //val p = PageRequest.of(0, 20)
        event.items.add(item1)
        event.items.add(item2)
        itemRepo.save(item1)
        itemRepo.save(item2)
        eventRepo.save(event)


        val item1Response = ItemResponseEntity(item1.id, item1.name, item1.marked)
        val item2Response = ItemResponseEntity(item2.id, item2.name, item2.marked)

        val items = listOf<ItemResponseEntity>(item1Response, item2Response)

        val res = eventService.getItems(event.id, authPerson)

        assertThat(res[0], `is`(equalTo(items[0])))
        assertThat(res[1], `is`(equalTo(items[1])))

    }

    @Test
    @Rollback
    @Transactional
    //complete
    fun deleteItemsTest() {
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

        val item1Name = "item1"
        val item2Name = "item2"
        val item1Marked = true
        val item2Marked = false

        val person = personRepo.save(Person(personName, personNickname, personPassword, secretWord, gender, userCity))
        personRepo.flush()
        val event = eventRepo.save(Event(name, game, city, address, date, maxPersonCount, person))
        eventRepo.flush()
        val item1 = itemRepo.save(Item(item1Name, item1Marked))
        itemRepo.flush()
        val item2 = itemRepo.save(Item(item2Name, item2Marked))
        itemRepo.flush()
        val authPerson = JwtPerson(person.id, "Vanius", "1234", listOf())

        item1.event = event
        item2.event = event
        itemRepo.save(item1)
        itemRepo.save(item2)

        eventService.deleteItems(event.id, authPerson)

        val resEvent = eventRepo.findEventById(event.id).get()

        assertThat(resEvent.items.size, `is`(equalTo(0)))

    }


    @Test
    @Rollback
    @Transactional
    //complete
    fun markItemsTest() {
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

        val item1Name = "item1"
        val item1Marked = false

        val mark1 = true

        val person = personRepo.save(Person(personName, personNickname, personPassword, secretWord, gender, userCity))
        personRepo.flush()
        val event = eventRepo.save(Event(name, game, city, address, date, maxPersonCount, person))
        eventRepo.flush()
        val item1 = itemRepo.save(Item(item1Name, item1Marked))
        itemRepo.flush()
        val authPerson = JwtPerson(person.id, "Vanius", "1234", listOf())

        event.members.add(person)
        event.items.add(item1)

        val markItemRequest1 = MarkItemRequestEntity(item1.id, mark1)

        eventService.markItem(event.id, markItemRequest1, authPerson)

        assertThat(event.items[0].marked, `is`(equalTo(true)))
    }
}