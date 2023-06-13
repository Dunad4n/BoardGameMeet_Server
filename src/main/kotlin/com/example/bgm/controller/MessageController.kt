package com.example.bgm.controller

import com.example.bgm.controller.dto.CreateMessageRequestEntity
import com.example.bgm.controller.dto.ItemResponseEntity
import com.example.bgm.controller.dto.MessageResponseEntity
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.services.MessageService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class MessageController {

    @Autowired
    private lateinit var messageService: MessageService

    @GetMapping("messagesIn/{eventId}")
    @Operation(summary = "Получения всех сообщений в чате", description = "В пагинации указывается только page и size")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", content = [(Content(mediaType = "application/json", array = (ArraySchema(schema = Schema(implementation = MessageResponseEntity::class)))))]),
        ApiResponse(responseCode = "471", description = "Мероприятия с таким id не существует"),
        ApiResponse(responseCode = "473", description = "Пользователь может прочитать сообщения только в том мероприятии, где он - участник")
    ])
    fun getAllMessages(@PathVariable @Parameter(description = "Id мероприятия") eventId: Long,
                       @AuthenticationPrincipal authPerson: JwtPerson,
                       @PageableDefault() pageable: Pageable): ResponseEntity<*> {
        return messageService.getMessages(eventId, authPerson, pageable)
    }
}