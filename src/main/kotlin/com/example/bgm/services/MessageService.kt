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

    private fun mapToMessageResponseEntity(message: Message): MessageResponseEntity {
        return MessageResponseEntity(message.text,
                                     eventId = message.event.id,
                                     nickname = message.person.nickname,
                                     name = message.person.name,
                                     avatarId = message.person.avatarId)
    }

    fun getMessages(eventId: Long, authPerson: JwtPerson): ArrayList<MessageResponseEntity> {
        val event = eventRepo.findById(eventId).get()
        if (!event.members.contains(personRepo.findByNickname(authPerson.username))) {
            throw Exception("person can read messages only from event where he is member")
        }
        val messages = arrayListOf<MessageResponseEntity>()
        for (message in event.messages) {
            messages.add(mapToMessageResponseEntity(message))
        }
        return messages
    }

    fun createMessage(createMessageRequest: CreateMessageRequestEntity): MessageResponseEntity {
        val person = personRepo.findByNickname(createMessageRequest.personNickname)
            ?: throw Exception("person with nickname ${createMessageRequest.personNickname} does not exist")
        val event = eventRepo.findById(createMessageRequest.eventId).get()
        val message = messageRepo.save(Message(createMessageRequest.text, LocalDateTime.now(), person, event))
        return mapToMessageResponseEntity(message)
    }
}