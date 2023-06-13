package com.example.bgm.services

import com.example.bgm.IntegrationEnvironment
import com.example.bgm.controller.dto.CreateMessageRequestEntity
import com.example.bgm.controller.dto.MessageResponseEntity
import com.example.bgm.entities.Event
import com.example.bgm.entities.Message
import com.example.bgm.entities.Person
import com.example.bgm.entities.enums.Gender
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.repositories.EventRepo
import com.example.bgm.repositories.MessageRepo
import com.example.bgm.repositories.PersonRepo
import com.example.bgm.repositories.RoleRepo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@SpringBootTest
class MessageServiceTest: IntegrationEnvironment()
{
    @Autowired private lateinit var messageService: MessageService
    @Autowired private lateinit var messageRepo: MessageRepo
    @Autowired private lateinit var personRepo: PersonRepo
    @Autowired private lateinit var eventRepo: EventRepo
    @Autowired private lateinit var roleRepo: RoleRepo

    @BeforeEach
    @Transactional
    fun clean() {
        eventRepo.deleteAll()
        personRepo.deleteAll()
        messageRepo.deleteAll()
    }

    @Test
    @Rollback
    @Transactional
    //complete
    fun createMessageTest() {
        /** given **/
        val name = "event"
        val game = "game"
        val city = "Voronezh"
        val maxPersonCount = 10
        val address = "address"
        val eventDate = LocalDateTime.now()

        val personName = "Ivan"
        val personNickname = "Vanius"
        val personPassword = "1234"
        val secretWord = "secret"
        val gender = Gender.MALE
        val userCity = "Voronezh"

        val text = "text"

        val person = personRepo.save(Person(personName, personNickname, personPassword, secretWord, gender, userCity))
        person.roles.add(roleRepo.findByName("ROLE_ADMIN"))
        personRepo.flush()
        val event = Event(name, game, city, address, eventDate, maxPersonCount, person)
        event.host = person
        event.members.add(person)
        eventRepo.save(event)
        eventRepo.flush()


        /** when **/
        val message = messageService.createMessage(CreateMessageRequestEntity(text, event.id!!, person.nickname)).body as MessageResponseEntity

        /** then **/
        assertThat(message.text, `is`(equalTo(text)))
    }

    @Test
    @Rollback
    @Transactional
    // TODO: pageable
    fun getMessagesTest() {
        /** given **/
        val name = "event"
        val game = "game"
        val city = "Voronezh"
        val maxPersonCount = 10
        val address = "address"
        val eventDate = LocalDateTime.now()

        val personName = "Ivan"
        val personNickname = "Vanius"
        val personPassword = "1234"
        val secretWord = "secret"
        val gender = Gender.MALE
        val userCity = "Voronezh"

        val text = "text"
        val time = LocalDateTime.now()

        val person = personRepo.save(Person(personName, personNickname, personPassword, secretWord, gender, userCity))
        personRepo.flush()
        val event = eventRepo.save(Event(name, game, city, address, eventDate, maxPersonCount, person))
        eventRepo.flush()
        val msg = messageRepo.save(Message(text, time, person, event))
        messageRepo.flush()
        val authPerson = JwtPerson(person.id, personNickname, personPassword, listOf())
        val p = PageRequest.of(0, 20)
        event.members.add(person)

        event.messages.add(msg)
        eventRepo.save(event)

        val msg1 = MessageResponseEntity(text, event.id, person.nickname, person.name, null)
        val messageList = arrayListOf<MessageResponseEntity>()
        messageList.add(msg1)

        /** when **/
        val messages = messageService.getMessages(event.id!!, authPerson, p).body as List<*>

        /** then **/
        assertThat(messages[0], `is`(equalTo(messageList[0])))
    }
}