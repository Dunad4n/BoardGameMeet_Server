package com.example.bgm.controller

import com.example.bgm.controller.dto.MessageResponseEntity
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.services.MessageService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
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
    fun getAllMessages(@PathVariable @Parameter(description = "Id мероприятия") eventId: Long,
                       @AuthenticationPrincipal authPerson: JwtPerson,
                       @PageableDefault()@Parameter(description = "Пагинация") pageable: Pageable
    ): ArrayList<MessageResponseEntity> {
        return messageService.getMessages(eventId, authPerson, pageable)
    }
}