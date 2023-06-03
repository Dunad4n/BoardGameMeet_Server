package com.example.bgm.services

import com.example.bgm.controller.dto.CreateMessageRequestEntity
import com.example.bgm.controller.dto.MessageResponseEntity
import com.example.bgm.entities.Message
import com.example.bgm.entities.Person
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.repositories.EventRepo
import com.example.bgm.repositories.MessageRepo
import com.example.bgm.repositories.PersonRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class MessageService {

    @Autowired
    lateinit var messageRepo: MessageRepo

    @Autowired
    lateinit var eventRepo: EventRepo

    @Autowired
    lateinit var personRepo: PersonRepo

    private fun mapToMessageResponseEntity(message: Message, person: Person?): MessageResponseEntity {
        return MessageResponseEntity(message.text,
                                     eventId = message.event.id,
                                     isMyNickname = message.person.nickname == message.person.nickname,
                                     name = message.person.name,
                                     avatarId = message.person.avatarId)
    }

    fun getMessages(eventId: Long, authPerson: JwtPerson, pageable: Pageable): ArrayList<MessageResponseEntity> {
        val event = eventRepo.findById(eventId).get()
        val person = personRepo.findByNickname(authPerson.username)
        if (!event.members.contains(person)) {
            throw Exception("person can read messages only from event where he is member")
        }
        val messages = arrayListOf<MessageResponseEntity>()
        val mess = messageRepo.findAllByEvent(event, pageable)
        for (message in mess) {
            messages.add(mapToMessageResponseEntity(message, message.person))
        }
        return messages
    }

    fun createMessage(createMessageRequest: CreateMessageRequestEntity): MessageResponseEntity {
        val person = personRepo.findByNickname(createMessageRequest.personNickname)
            ?: throw Exception("person with nickname ${createMessageRequest.personNickname} does not exist")
        val event = eventRepo.findById(createMessageRequest.eventId).get()
        val message = messageRepo.save(Message(createMessageRequest.text, LocalDateTime.now(), person, event))
        return mapToMessageResponseEntity(message, person)
    }
}