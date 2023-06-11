package com.example.bgm.controller

import com.example.bgm.controller.dto.CreateMessageRequestEntity
import com.example.bgm.controller.dto.ItemResponseEntity
import com.example.bgm.controller.dto.MessageResponseEntity
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.services.MessageService
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
class MessageController {

    @Autowired
    private lateinit var messageService: MessageService


    @GetMapping("messagesIn/{eventId}")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", content = [(Content(mediaType = "application/json", array = (ArraySchema(schema = Schema(implementation = MessageResponseEntity::class)))))]),
        ApiResponse(responseCode = "510", description = "Event with id not exist")
    ])
    fun getAllMessages(@PathVariable eventId: Long,
                       @AuthenticationPrincipal authPerson: JwtPerson,
                       @PageableDefault() pageable: Pageable): ResponseEntity<*> {
        return messageService.getMessages(eventId, authPerson, pageable)
    }


//    @PostMapping("createMessage")
//    fun createMessage(@RequestBody createMessageRequest: CreateMessageRequestEntity,
//                      @AuthenticationPrincipal authPerson: JwtPerson): MessageResponseEntity {
//        return messageService.createMessage(createMessageRequest, authPerson)
//    }

}