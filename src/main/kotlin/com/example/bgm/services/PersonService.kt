package com.example.bgm.services

import com.example.bgm.controller.dto.MemberResponseEntity
import com.example.bgm.controller.dto.ProfileResponseEntity
import com.example.bgm.controller.dto.UpdatePersonRequestEntity
import com.example.bgm.entities.Event
import com.example.bgm.entities.Person
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.repositories.EventRepo
import com.example.bgm.repositories.PersonRepo
import com.example.bgm.repositories.RoleRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class PersonService {

    @Autowired
    private lateinit var personRepo: PersonRepo

    @Autowired
    private lateinit var eventRepo: EventRepo

    @Autowired
    private lateinit var roleRepo: RoleRepo

    private val encoder = BCryptPasswordEncoder()


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
    fun updatePerson(updateRequest: UpdatePersonRequestEntity, authPerson: JwtPerson) {
        val person = personRepo.findByNickname(authPerson.username)
            ?: throw Exception("person with nickaname ${authPerson.username} does not exist")
        person.name = updateRequest.name
        person.nickname = updateRequest.nickname
        person.city = updateRequest.city
        person.age = updateRequest.age
        person.avatarId = updateRequest.avatarId
        personRepo.save(person)
    }

    fun deletePerson(nickname: String, authPerson: JwtPerson) {
        val person = personRepo.findByNickname(authPerson.username)
            ?: throw Exception("person with nickname ${authPerson.username} does not exist")
        if (!person.roles.contains(roleRepo.findByName("ROLE_ADMIN"))) {
            throw Exception("only admin can delete users")
        }
        personRepo.deleteByNickname(nickname)
    }

    fun getAllMembers(eventId: Long): ArrayList<MemberResponseEntity> {
        val event = eventRepo.findById(eventId).get()
        val members = arrayListOf<MemberResponseEntity>()
        for (person in event.members) {
            members.add(mapToMemberResponseEntity(person, event))
        }
        return members
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

    fun validateSecretWord(secretWord: String, authPerson: JwtPerson): ResponseEntity<String> {
        if (!personRepo.findByNickname(authPerson.username)?.secretWord.equals(secretWord)) {
            return ResponseEntity.badRequest().body("wrong secret word")
        }
        return ResponseEntity.ok("correct secret word")
    }

    fun changePassword(newPassword: String, repeatNewPassword: String, authPerson: JwtPerson) {
        if (newPassword != repeatNewPassword) {
            throw Exception("passwords is not equals")
        }
        val person = personRepo.findByNickname(authPerson.username)
            ?: throw Exception("can not find person with nickname ${authPerson.username}")
        person.password = encoder.encode(newPassword)
        personRepo.save(person)
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