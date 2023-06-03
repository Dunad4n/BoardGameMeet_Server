package com.example.bgm.controller

import com.example.bgm.controller.dto.CreateMessageRequestEntity
import com.example.bgm.controller.dto.MessageResponseEntity
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.repositories.PersonRepo
import com.example.bgm.services.MessageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Controller
class ChatController {

    @Autowired
    private lateinit var messageService: MessageService

    @Autowired
    private lateinit var personRepo: PersonRepo

    @MessageMapping("/chat")
//    @SendTo("/topic/chat")
    fun chatting(
        @Payload messageRequestEntity: CreateMessageRequestEntity): MessageResponseEntity {
        println(messageRequestEntity.text)
        println(messageRequestEntity.eventId)
        println(messageRequestEntity.personNickname)
        val message = messageService.createMessage(messageRequestEntity)
        val person = personRepo.findByNickname(messageRequestEntity.personNickname)
            ?: throw Exception("person with nickname ${messageRequestEntity.personNickname} not exist")
        return message
    }

}