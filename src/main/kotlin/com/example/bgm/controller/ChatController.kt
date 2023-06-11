package com.example.bgm.controller

import com.example.bgm.controller.dto.CreateMessageRequestEntity
import com.example.bgm.controller.dto.MessageResponseEntity
import com.example.bgm.repositories.PersonRepo
import com.example.bgm.services.MessageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller

@Controller
class ChatController {

    @Autowired private lateinit var messagingTemplate: SimpMessagingTemplate
    @Autowired private lateinit var messageService: MessageService
    @Autowired private lateinit var personRepo: PersonRepo

    @MessageMapping("/chat")
//    @SendTo("/topic/chat")
    fun chatting(
        @Payload messageRequestEntity: CreateMessageRequestEntity) {
        println(messageRequestEntity.text)
        println(messageRequestEntity.eventId)
        println(messageRequestEntity.personNickname)
        val message = messageService.createMessage(messageRequestEntity)
        messagingTemplate.convertAndSend(
                message["eventId"] as String, "/topic/chat", message
        )
    }

}