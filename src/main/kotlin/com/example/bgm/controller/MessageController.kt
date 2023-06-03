package com.example.bgm.controller

import com.example.bgm.controller.dto.CreateMessageRequestEntity
import com.example.bgm.controller.dto.MessageResponseEntity
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.services.MessageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
class MessageController {

    @Autowired
    private lateinit var messageService: MessageService


    @GetMapping("messagesIn/{eventId}")
    fun getAllMessages(@PathVariable eventId: Long,
                       @AuthenticationPrincipal authPerson: JwtPerson,
                       @PageableDefault() pageable: Pageable): ArrayList<MessageResponseEntity> {
        return messageService.getMessages(eventId, authPerson, pageable)
    }


//    @PostMapping("createMessage")
//    fun createMessage(@RequestBody createMessageRequest: CreateMessageRequestEntity,
//                      @AuthenticationPrincipal authPerson: JwtPerson): MessageResponseEntity {
//        return messageService.createMessage(createMessageRequest, authPerson)
//    }

}