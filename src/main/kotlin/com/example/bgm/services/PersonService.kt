package com.example.bgm.services

import com.example.bgm.controller.dto.*
import com.example.bgm.entities.Event
import com.example.bgm.entities.Person
import com.example.bgm.entities.jwt.Token
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.jwt.JwtTokenProvider
import com.example.bgm.repositories.EventRepo
import com.example.bgm.repositories.PersonRepo
import com.example.bgm.repositories.RoleRepo
import com.example.bgm.repositories.jwt.TokenRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PersonService {

    @Autowired
    private lateinit var personRepo: PersonRepo

    @Autowired
    private lateinit var eventRepo: EventRepo

    @Autowired
    private lateinit var roleRepo: RoleRepo

    @Autowired
    lateinit var tokenRepo: TokenRepo

    @Autowired
    lateinit var encoder: BCryptPasswordEncoder


    private fun mapToMemberResponseEntity(person: Person, event: Event): MemberResponseEntity {
        return MemberResponseEntity(person.nickname,
                                    person.avatarId,
                               person == event.host)
    }

    private fun mapToProfileResponseEntity(person: Person): ProfileResponseEntity {
        return ProfileResponseEntity(person.name,
                                     person.nickname,
                                     person.age,
                                     person.city,
                                     person.avatarId,
                                     person.gender)
    }


    fun getPerson(id: Long?): Person? {
        if (id == null) {
            throw Exception("id can not be null")
        }
        return personRepo.findById(id).get()
    }

    /**
     * обсудить реквесты save update
     */
    @Transactional
    fun updatePerson(updateRequest: UpdatePersonRequestEntity,
                     authPerson: JwtPerson,
                     jwtTokenProvider: JwtTokenProvider): ResponseEntity<*> {
        val person = personRepo.findByNickname(authPerson.username)
            ?: throw Exception("person with nickname ${authPerson.username} does not exist")
        val isPersonWithRequestedNicknameExist = personRepo.existsByNickname(updateRequest.nickname)
        if (isPersonWithRequestedNicknameExist && authPerson.username != updateRequest.nickname) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Такой никнейм уже занят")
        }
        person.name = updateRequest.name
        person.nickname = updateRequest.nickname
        person.city = updateRequest.city
        person.age = updateRequest.age
        person.gender = updateRequest.gender
        person.avatarId = updateRequest.avatarId
        personRepo.save(person)

        tokenRepo.deleteAllByPerson(person)
        val token = jwtTokenProvider.createToken(person.nickname, person.roles)
        SecurityContextHolder.getContext().authentication = jwtTokenProvider.getAuthentication(token)
        tokenRepo.save(Token(token, person))

        val response = mutableMapOf<String, String>()
        response["token"] = token
        response["nickname"] = person.nickname
        return ResponseEntity.ok(response)
    }

    @Transactional
    fun deletePerson(nickname: String, authPerson: JwtPerson): ResponseEntity<*> {
        val person = personRepo.findByNickname(authPerson.username)
            ?: throw Exception("person with nickname ${authPerson.username} not exist")
        if (!personRepo.existsByNickname(nickname)) {
            return ResponseEntity.status(511).body("person with nickname $nickname not exist")
        }
        if (!person.roles.contains(roleRepo.findByName("ROLE_ADMIN"))) {
            throw Exception("only admin can delete users")
        }
        personRepo.deleteByNickname(nickname)
        return ResponseEntity.ok("done")
    }

    fun getAllMembers(eventId: Long, pageable: Pageable, authPerson: JwtPerson): ResponseEntity<*> {
        if (!eventRepo.existsById(eventId)) {
            return ResponseEntity.status(510).body("event with id $eventId not exist")
        }
        val person = personRepo.findByNickname(authPerson.username)
            ?: throw Exception("person with nickname ${authPerson.username} not exist")
        val event = eventRepo.findById(eventId).get()
        if (!event.members.contains(person) && !person.roles.contains(roleRepo.findByName("ROLE_ADMIN"))) {
            return ResponseEntity.status(512).body("only mmber or admin can get all members")
        }
        val members = personRepo.findAllByEventsContainingOrderByHostIn(event, pageable)
        val res = arrayListOf<MemberResponseEntity>()
        for (member in members) {
            res.add(mapToMemberResponseEntity(member, event))
        }
        val response = mutableMapOf<Any, Any>()
        response["members"] = res
        response["membersCount"] = res.size
        return ResponseEntity.ok(response)
    }

    fun getProfile(nickname: String): ResponseEntity<*> {
        val person = personRepo.findByNickname(nickname)
            ?: return ResponseEntity.status(511).body("person with nickname $nickname not exist")
        return ResponseEntity.ok(mapToProfileResponseEntity(person))
    }

    fun getByNickname(nickname: String): Person {
        return personRepo.findByNickname(nickname)
            ?: throw UsernameNotFoundException("person with nickname $nickname not found")
    }

    fun joinToEvent(userId: Long, eventId: Long): ResponseEntity<*> {
        if (!eventRepo.existsById(eventId)) {
            return ResponseEntity.status(510).body("event with id $eventId not exist")
        }
        val event = eventRepo.findById(eventId).get()
        val person = personRepo.findById(userId).get()
//        if (!event.bannedMembers.contains(person) && !event.members.contains(person)) {
        event.addPerson(person)
        eventRepo.save(event)
//        } else {
//            throw Exception("this person can not join to chosen event")
//        }
        return ResponseEntity.ok("done")
    }

    fun leaveFromEvent(userId: Long, eventId: Long): ResponseEntity<*> {
        if (!eventRepo.existsById(eventId)) {
            return ResponseEntity.status(510).body("event with id $eventId not exist")
        }
        val person = personRepo.findById(userId).get()
        val event = eventRepo.findById(eventId).get()
        if (event.members.contains(person) && event.host.id != person.id) {
            event.kick(person)
            eventRepo.save(event)
        } else {
            if (!person.roles.contains(roleRepo.findByName("ROLE_ADMIN"))) {
                return ResponseEntity.status(512).body("this person can not leave from chosen event")
            }
        }
        return ResponseEntity.ok("done")
    }

    fun validateSecretWord(secretWord: String, nickname: String): ResponseEntity<String> {
        if (!personRepo.findByNickname(nickname)?.secretWord.equals(secretWord)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Неверное секретное слово или никнейм")
        }
        return ResponseEntity.ok("correct secret word")
    }

    fun changePassword(nickname: String, newPassword: String, repeatNewPassword: String) {
        if (newPassword != repeatNewPassword) {
            throw Exception("passwords is not equals")
        }
        val person = personRepo.findByNickname(nickname)
            ?: throw Exception("can not find person with nickname $nickname")
        person.password = encoder.encode(newPassword)
        personRepo.save(person)
    }

    fun isMyProfile(nickname: String, authPerson: JwtPerson): IsMyProfileResponseEntity {
        val status = nickname == authPerson.username
        return IsMyProfileResponseEntity(status)
    }

    fun verifyToken(token: String, nickname: String): Boolean {
        val person = personRepo.findByNickname(nickname)
            ?: throw Exception("can not find person with nickname $nickname")
        val tokens = tokenRepo.findAllByPerson(person)
        return tokens[tokens.size - 1].value == token
    }

    fun isMemberOfEvent(eventId: Long, authPerson: JwtPerson):ResponseEntity<*> {
        if (!eventRepo.existsById(eventId)) {
            return ResponseEntity.status(510).body("event with id $eventId not exist")
        }
        val person = personRepo.findByNickname(authPerson.username)
        val event = eventRepo.findEventById(eventId).get()
        return if (event.members.contains(person)) {
            ResponseEntity.ok(true)
        } else {
            ResponseEntity.ok(false)
        }
    }
}