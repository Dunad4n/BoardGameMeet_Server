package com.example.bgm.controller

import com.example.bgm.controller.dto.CreateMessageRequestEntity
import com.example.bgm.controller.dto.MessageResponseEntity
import com.example.bgm.services.MessageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class MessageController {

    @Autowired
    private lateinit var messageService: MessageService


    @GetMapping("messagesIn/{eventId}")
    fun getAllMessages(@PathVariable eventId: Long): ArrayList<MessageResponseEntity> {
        return messageService.getMessages(eventId)
    }


    @PostMapping("createMessage")
    fun createMessage(createMessageRequest: CreateMessageRequestEntity) {
        return messageService.createMessage(createMessageRequest)
    }

}