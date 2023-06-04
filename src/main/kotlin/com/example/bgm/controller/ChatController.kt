package com.example.bgm.controller

import com.example.bgm.controller.dto.CreateMessageRequestEntity
import com.example.bgm.controller.dto.MessageResponseEntity
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.repositories.PersonRepo
import com.example.bgm.services.MessageService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Controller
@Tag(name = "Контроллер чата", description="Сообщения в чатах")
class ChatController {

    @Autowired
    private lateinit var messageService: MessageService

    @Autowired
    private lateinit var personRepo: PersonRepo

    @MessageMapping("/chat")
//    @SendTo("/topic/chat")
    fun chatting(
        @Payload messageRequestEntity: CreateMessageRequestEntity,
    ): MutableMap<String, String> {
        println(messageRequestEntity.text)
        println(messageRequestEntity.eventId)
        println(messageRequestEntity.personNickname)
        val message = messageService.createMessage(messageRequestEntity)
        val person = personRepo.findByNickname(messageRequestEntity.personNickname)
            ?: throw Exception("person with nickname ${messageRequestEntity.personNickname} not exist")
        val map = mutableMapOf<String, String>()
        map["text"] = message.text
        map["eventId"] = messageRequestEntity.eventId.toString()
        map["nickname"] = messageRequestEntity.personNickname
        map["name"] = person.name
        map["avatarId"] = person.avatarId.toString()
        return map
    }

    @GetMapping("messagesIn/{eventId}")
    @Operation(summary = "Получения всех сообщений в чате", description = "В пагинации указывается только page и size")
    fun getAllMessages(@PathVariable@Parameter(description = "Id мероприятия") eventId: Long,
                       @AuthenticationPrincipal authPerson: JwtPerson,
                       @PageableDefault()@Parameter(description = "Пагинация") pageable: Pageable
    ): ArrayList<MessageResponseEntity> {
        return messageService.getMessages(eventId, authPerson, pageable)
    }
}