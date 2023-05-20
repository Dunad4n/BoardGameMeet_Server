package com.example.bgm.services

import com.example.bgm.controller.dto.CreateMessageRequestEntity
import com.example.bgm.entities.Event
import com.example.bgm.entities.Message
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
import java.time.LocalDateTime

@SpringBootTest
class MessageServiceTest
{
    @Autowired
    private lateinit var messageService: MessageService

    @Autowired
    private lateinit var messageRepo: MessageRepo

    @Autowired
    private lateinit var personRepo: PersonRepo

    @Autowired
    private lateinit var eventRepo: EventRepo

    @Test
    @Rollback
    @Transactional
    // TODO: ден доделает потом хлопнуть тест 
    fun createMessageTest() {
        /** given **/
        val personName = "Ivan"
        val personNickname = "Vanius"
        val personPassword = "1234"
        val secretWord = "secret"
        val gender = Gender.MALE
        val userCity = "Voronezh"

        val text = "text"

        val person = personRepo.save(Person(personName, personNickname, personPassword, secretWord, gender, userCity))
        personRepo.flush()

        /** when **/
        messageService.createMessage(CreateMessageRequestEntity(text, person.id))
        val message = messageRepo.findByPerson(person)

        /** then **/
        assertThat(message.text, `is`(equalTo(text)))
        //assertThat(message., `is`(equalTo(text)))
    }

    @Test
    @Rollback
    @Transactional
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
        val messageDate = LocalDateTime.now()

        val person = personRepo.save(Person(personName, personNickname, personPassword, secretWord, gender, userCity))
        personRepo.flush()

        val event = eventRepo.save(Event(name, game, city, address, eventDate, maxPersonCount, person))
        eventRepo.flush()
        val msg = Message(text, messageDate, person)
        val message = messageRepo.save(msg)
        messageRepo.flush()

        event.messages.add(message)

        /** when **/
        val messages = messageService.getMessages(event.id)

        /** then **/
        //assertThat(messages[0], `is`(equalTo(message)))
    }
}