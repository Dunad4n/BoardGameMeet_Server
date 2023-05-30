package com.example.bgm.controller

import com.example.bgm.controller.dto.CreateMessageRequestEntity
import com.example.bgm.controller.dto.MessageResponseEntity
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.services.MessageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ChatController {

    @Autowired
    private lateinit var messageService: MessageService

    @MessageMapping("createMessage")
    @SendTo("/topic/chat")
    fun chatting(@Payload messageRequestEntity: CreateMessageRequestEntity,
                 @AuthenticationPrincipal authPerson: JwtPerson): MessageResponseEntity {
        Thread.sleep(1000)
        return messageService.createMessage(messageRequestEntity, authPerson)
    }

}