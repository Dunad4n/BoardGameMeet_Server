package com.example.bgm.controller

import com.example.bgm.controller.dto.CreateMessageRequestEntity
import com.example.bgm.controller.dto.MessageResponseEntity
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.services.MessageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
class MessageController {

    @Autowired
    private lateinit var messageService: MessageService


    @GetMapping("messagesIn/{eventId}")
    fun getAllMessages(@PathVariable eventId: Long): ArrayList<MessageResponseEntity> {
        return messageService.getMessages(eventId)
    }


    @PostMapping("createMessage")
    fun createMessage(@RequestBody createMessageRequest: CreateMessageRequestEntity,
                      @AuthenticationPrincipal authPerson: JwtPerson): MessageResponseEntity {
        return messageService.createMessage(createMessageRequest, authPerson)
    }

}