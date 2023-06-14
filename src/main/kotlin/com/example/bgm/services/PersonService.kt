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

    @Autowired private lateinit var personRepo: PersonRepo
    @Autowired private lateinit var eventRepo: EventRepo
    @Autowired private lateinit var roleRepo: RoleRepo
    @Autowired private lateinit var tokenRepo: TokenRepo
    @Autowired private lateinit var encoder: BCryptPasswordEncoder


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

        return ResponseEntity.ok(UpdateProfileEntity(token, person.nickname))
    }

    @Transactional
    fun deletePerson(nickname: String, authPerson: JwtPerson): ResponseEntity<*> {
        val person = personRepo.findByNickname(authPerson.username)
            ?: throw Exception("person with nickname ${authPerson.username} not exist")
        if (!personRepo.existsByNickname(nickname)) {
            return ResponseEntity.status(472).body("person with nickname $nickname not exist")
        }
        if (!person.roles.contains(roleRepo.findByName("ROLE_ADMIN"))) {
            throw Exception("only admin can delete users")
        }
        personRepo.deleteByNickname(nickname)
        return ResponseEntity.ok(HttpStatus.OK)
    }

    fun getAllMembers(eventId: Long, pageable: Pageable, authPerson: JwtPerson): ResponseEntity<*> {
        if (!eventRepo.existsById(eventId)) {
            return ResponseEntity.status(471).body("event with id $eventId not exist")
        }
        val person = personRepo.findByNickname(authPerson.username)
            ?: throw Exception("person with nickname ${authPerson.username} not exist")
        val event = eventRepo.findById(eventId).get()
        if (!event.members.contains(person) && !person.roles.contains(roleRepo.findByName("ROLE_ADMIN"))) {
            return ResponseEntity.status(473).body("only member or admin can get all members")
        }
        println(event.host)
        val res = arrayListOf<MemberResponseEntity>()
        res.add(mapToMemberResponseEntity(event.host, event))
        for (member in personRepo.findAllByEventsContaining(event)) {
            if(member != event.host) {
                res.add(mapToMemberResponseEntity(member, event))
            }
        }

        println(res)

        val from = if(pageable.pageNumber * pageable.pageSize < res.size) pageable.pageNumber * pageable.pageSize else res.size - 1
        val to = if((pageable.pageNumber + 1) * pageable.pageSize < res.size) (pageable.pageNumber + 1) * pageable.pageSize else res.size - 1

        println(res.subList(from, to + 1))

        return ResponseEntity.ok(res.subList(from, to + 1))
    }

    fun getProfile(nickname: String): ResponseEntity<*> {
        val person = personRepo.findByNickname(nickname)
            ?: return ResponseEntity.status(472).body("person with nickname $nickname not exist")
        return ResponseEntity.ok(mapToProfileResponseEntity(person))
    }

    fun getByNickname(nickname: String): Person {
        return personRepo.findByNickname(nickname)
            ?: throw UsernameNotFoundException("person with nickname $nickname not found")
    }

    fun joinToEvent(userId: Long, eventId: Long): ResponseEntity<*> {
        if (!eventRepo.existsById(eventId)) {
            return ResponseEntity.status(471).body("event with id $eventId not exist")
        }
        val event = eventRepo.findById(eventId).get()
        val person = personRepo.findById(userId).get()
        event.addPerson(person)
        eventRepo.save(event)
        return ResponseEntity.ok("done")
    }

    fun leaveFromEvent(userId: Long, eventId: Long): ResponseEntity<*> {
        if (!eventRepo.existsById(eventId)) {
            return ResponseEntity.status(471).body("event with id $eventId not exist")
        }
        val person = personRepo.findById(userId).get()
        val event = eventRepo.findById(eventId).get()
        if (event.members.contains(person) && event.host.id != person.id) {
            event.kick(person)
            eventRepo.save(event)
        } else {
            if (!person.roles.contains(roleRepo.findByName("ROLE_ADMIN"))) {
                return ResponseEntity.status(473).body("this person can not leave from chosen event")
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

    fun verifyToken(token: String, nickname: String): ResponseEntity<*> {
        val person = personRepo.findByNickname(nickname)
            ?: throw Exception("can not find person with nickname $nickname")
        val tokens = tokenRepo.findAllByPerson(person)
        return ResponseEntity.ok(tokens[tokens.size - 1].value == token)
    }

    fun isMemberOfEvent(eventId: Long, authPerson: JwtPerson):ResponseEntity<*> {
        if (!eventRepo.existsById(eventId)) {
            return ResponseEntity.status(471).body("event with id $eventId not exist")
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