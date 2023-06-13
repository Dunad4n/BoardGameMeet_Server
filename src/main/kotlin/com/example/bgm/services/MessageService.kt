package com.example.bgm.services

import com.example.bgm.controller.dto.CreateMessageRequestEntity
import com.example.bgm.controller.dto.MessageResponseEntity
import com.example.bgm.entities.Message
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.repositories.EventRepo
import com.example.bgm.repositories.MessageRepo
import com.example.bgm.repositories.PersonRepo
import com.example.bgm.repositories.RoleRepo
import org.apache.coyote.Response
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

    private fun mapToMessageResponseEntity(message: Message): MessageResponseEntity {
        return MessageResponseEntity(text = message.text,
                                     eventId = message.event.id,
                                     myNickname = message.person.nickname,
                                     name = message.person.name,
                                     avatarId = message.person.avatarId)
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
        val messages = arrayListOf<MessageResponseEntity>()
        val mess = messageRepo.findAllByEventOrderByDateTimeDesc(event, pageable)
        for (message in mess) {
            messages.add(mapToMessageResponseEntity(message))
        }
        return ResponseEntity.ok(messages)
    }

    fun createMessage(createMessageRequest: CreateMessageRequestEntity): ResponseEntity<*> {
        val person = personRepo.findByNickname(createMessageRequest.personNickname)
            ?: return ResponseEntity.status(511).body("person with nickname ${createMessageRequest.personNickname} not exist")
        if (!eventRepo.existsById(createMessageRequest.eventId)) {
            return ResponseEntity.status(510).body("event with id ${createMessageRequest.eventId} not exist")
        }
        val event = eventRepo.findById(createMessageRequest.eventId).get()
        if (!event.members.contains(person)) {
            return ResponseEntity.status(512).body("only member or admin can get all members")
        }
        val message = messageRepo.save(Message(createMessageRequest.text, LocalDateTime.now(), person, event))
        return ResponseEntity.ok(mapToMessageResponseEntity(message))
    }
}