package com.example.bgm.controller

import com.example.bgm.controller.dto.CreateMessageRequestEntity
import com.example.bgm.repositories.PersonRepo
import com.example.bgm.services.MessageService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller

@Controller
@Tag(name = "Контроллер чата", description="Сообщения в чатах")
class ChatController {

    @Autowired private lateinit var messageService: MessageService

    @MessageMapping("/chat")
    fun chatting(@Payload messageRequestEntity: CreateMessageRequestEntity): ResponseEntity<*> {
        println(messageRequestEntity.text)
        println(messageRequestEntity.eventId)
        println(messageRequestEntity.personNickname)
        return messageService.createMessage(messageRequestEntity)
    }
}