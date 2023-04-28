package com.example.bgm.services

import com.example.bgm.Controller.MessageResponseEntity
import com.example.bgm.entities.Message
import com.example.bgm.entities.Person
import com.example.bgm.repositories.EventRepo
import com.example.bgm.repositories.MessageRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MessageService {

    @Autowired
    lateinit var messageRepo: MessageRepo

    @Autowired
    lateinit var eventRepo: EventRepo

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

}