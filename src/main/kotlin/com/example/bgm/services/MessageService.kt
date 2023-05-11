package com.example.bgm.services

import com.example.bgm.controller.dto.CreateMessageRequestEntity
import com.example.bgm.controller.dto.MessageResponseEntity
import com.example.bgm.entities.Message
import com.example.bgm.entities.Person
import com.example.bgm.repositories.EventRepo
import com.example.bgm.repositories.MessageRepo
import com.example.bgm.repositories.PersonRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class MessageService {

    @Autowired
    private lateinit var messageRepo: MessageRepo

    @Autowired
    private lateinit var eventRepo: EventRepo

    @Autowired
    private lateinit var personRepo: PersonRepo

    private fun mapToMessageResponseEntity(message: Message, person: Person): MessageResponseEntity {
        return MessageResponseEntity(message.text,
                                     person.avatarId)
    }

    fun getMessages(eventId: Long): ArrayList<MessageResponseEntity> {
        val event = eventRepo.findById(eventId).get()
        var messages = arrayListOf<MessageResponseEntity>()
        for (message in event.messages) {
            messages.add(mapToMessageResponseEntity(message, message.person))
        }
        return messages
    }

    fun createMessage(createMessageRequest: CreateMessageRequestEntity) {
        val user = personRepo.findById(createMessageRequest.userid).get()
        messageRepo.save(Message(createMessageRequest.text, LocalDateTime.now(), user))
    }
}