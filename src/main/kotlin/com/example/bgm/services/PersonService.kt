package com.example.bgm.services

import com.example.bgm.controller.dto.IsMyProfileResponseEntity
import com.example.bgm.controller.dto.MemberResponseEntity
import com.example.bgm.controller.dto.ProfileResponseEntity
import com.example.bgm.controller.dto.UpdatePersonRequestEntity
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
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class PersonService {

    @Autowired
    lateinit var personRepo: PersonRepo

    @Autowired
    lateinit var eventRepo: EventRepo

    @Autowired
    lateinit var roleRepo: RoleRepo

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


    fun getPerson(id: Long): Person? {
        return personRepo.findById(id).get()
    }

    /**
     * обсудить реквесты save update
     */
    @Transactional
    open fun updatePerson(updateRequest: UpdatePersonRequestEntity,
                          authPerson: JwtPerson,
                          jwtTokenProvider: JwtTokenProvider): Map<String, String> {
        val person = personRepo.findByNickname(authPerson.username)
            ?: throw Exception("person with nickname ${authPerson.username} does not exist")
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
        return response
    }

    fun deletePerson(nickname: String, authPerson: JwtPerson) {
        val person = personRepo.findByNickname(authPerson.username)
            ?: throw Exception("person with nickname ${authPerson.username} does not exist")
        if (!person.roles.contains(roleRepo.findByName("ROLE_ADMIN"))) {
            throw Exception("only admin can delete users")
        }
        personRepo.deleteByNickname(nickname)
    }

    fun getAllMembers(eventId: Long, pageable: Pageable): ArrayList<MemberResponseEntity> {
        val event = eventRepo.findById(eventId).get()
        val members = personRepo.findAllByEventsContaining(event, pageable)
        val res = arrayListOf<MemberResponseEntity>()
        for (person in members) {
            res. add(mapToMemberResponseEntity(person, event))
        }
        return res
    }

    fun getProfile(nickname: String): ProfileResponseEntity {
        val person = personRepo.findByNickname(nickname)
        if(person != null) {
            return mapToProfileResponseEntity(person)
        }
        throw Exception("There is no user with this nickname")
    }

    fun getByNickname(nickname: String): Person {
        return personRepo.findByNickname(nickname)
            ?: throw UsernameNotFoundException("person with nickname $nickname not found")
    }

    fun joinToEvent(userId: Long, eventId: Long) {
        val event = eventRepo.findById(eventId).get()
        val person = personRepo.findById(userId).get()
        if (!event.bannedMembers.contains(person) && !event.members.contains(person)) {
            event.addPerson(person)
            eventRepo.save(event)
        } else {
            throw Exception("this person can not join to chosen event")
        }
    }

    fun leaveFromEvent(userId: Long, eventId: Long) {
        val person = personRepo.findById(userId).get()
        val event = eventRepo.findById(eventId).get()
        if (event.members.contains(person) && event.host.id != person.id) {
            event.kick(person)
            eventRepo.save(event)
        } else {
            throw Exception("this person can not leave from chosen event")
        }
    }

    fun validateSecretWord(secretWord: String, nickname: String): ResponseEntity<String> {
        if (!personRepo.findByNickname(nickname)?.secretWord.equals(secretWord)) {
            return ResponseEntity.badRequest().body("wrong secret word")
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
        return tokens[token.length - 1].value == token
    }

//    fun register(person: Person): Person? {
//        val rolePerson: Role = roleRepo.findByName("ROLE_USER")
//        val personRoles = mutableListOf<Role>()
//        personRoles.add(rolePerson)
//        person.password = passwordEncoder.encode(person.password)
//        person.roles = personRoles
//        return personRepo.save(person)
//    }
}