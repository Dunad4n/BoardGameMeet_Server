package com.example.bgm.services

import com.example.bgm.controller.dto.CreateMessageRequestEntity
import com.example.bgm.controller.dto.MessageResponseEntity
import com.example.bgm.entities.Message
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.repositories.EventRepo
import com.example.bgm.repositories.MessageRepo
import com.example.bgm.repositories.PersonRepo
import com.example.bgm.repositories.RoleRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class MessageService {

    @Autowired
    lateinit var messageRepo: MessageRepo

    @Autowired
    lateinit var eventRepo: EventRepo

    @Autowired
    lateinit var personRepo: PersonRepo

    @Autowired
    lateinit var roleRepo: RoleRepo

    private fun mapToMessageResponseEntity(message: Message): Map<String, Any> {
        val responseMap = mutableMapOf<String, Any>()
        responseMap["text"] = message.text
        responseMap["eventId"] = message.event.id.toString()
        responseMap["myNickname"] = message.person.nickname
        responseMap["name"] = message.person.name
        responseMap["avatarId"] = message.person.avatarId.toString()
        return responseMap
    }

    fun getMessages(eventId: Long, authPerson: JwtPerson, pageable: Pageable): ResponseEntity<*> {
        if (!eventRepo.existsById(eventId)) {
            return ResponseEntity.status(510).body("event with id $eventId not exist")
        }
        val event = eventRepo.findById(eventId).get()
        val person = personRepo.findByNickname(authPerson.username)
            ?: throw Exception("person with nickname ${authPerson.username} does not exist")
        if (!event.members.contains(person) && !person.roles.contains(roleRepo.findByName("ROLE_ADMIN"))) {
            return ResponseEntity.status(512).body("person can read messages only from event where he is member")
        }
        val messages = arrayListOf<Map<String, Any>>()
        val mess = messageRepo.findAllByEventOrderByDateTimeDesc(event, pageable)
        for (message in mess) {
            messages.add(mapToMessageResponseEntity(message))
        }
        return ResponseEntity.ok(messages)
    }

    fun createMessage(createMessageRequest: CreateMessageRequestEntity): Map<String, Any> {
        val person = personRepo.findByNickname(createMessageRequest.personNickname)
            ?: throw Exception("person with nickname ${createMessageRequest.personNickname} does not exist")
//        val authPerson = SecurityContextHolder.getContext().authentication.principal as JwtPerson
        val event = eventRepo.findById(createMessageRequest.eventId).get()
        val message = messageRepo.save(Message(createMessageRequest.text, LocalDateTime.now(), person, event))
        return mapToMessageResponseEntity(message)
    }
}