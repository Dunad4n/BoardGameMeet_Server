package com.example.bgm.services

import com.example.bgm.controller.CreatePersonRequestEntity
import com.example.bgm.controller.MemberResponseEntity
import com.example.bgm.controller.ProfileResponseEntity
import com.example.bgm.controller.UpdatePersonRequestEntity
import com.example.bgm.entities.Event
import com.example.bgm.entities.Person
import com.example.bgm.entities.Role
import com.example.bgm.repositories.EventRepo
import com.example.bgm.repositories.PersonRepo
import com.example.bgm.repositories.RoleRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class PersonService {

    @Autowired
    lateinit var personRepo: PersonRepo

    @Autowired
    lateinit var eventRepo: EventRepo

    @Autowired
    lateinit var roleRepo: RoleRepo

//    @Autowired
//    lateinit var passwordEncoder: BCryptPasswordEncoder


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

    fun createPerson(createPersonRequest: CreatePersonRequestEntity) {
        if(personRepo.findByNickname(createPersonRequest.nickname) == null) {
            personRepo.save(
                Person(
                    createPersonRequest.name,
                    createPersonRequest.nickname,
                    createPersonRequest.password,
                    createPersonRequest.secretWord,
                    createPersonRequest.gender,
                    createPersonRequest.city
                )
            )
        } else {
            throw Exception("this nickname is occupied")
        }
    }

    /**
     * обсудить реквесты save update
     */
    fun updatePerson(updateRequest: UpdatePersonRequestEntity) {
        val person = personRepo.findById(updateRequest.id).get()
        person.name = updateRequest.name
        person.nickname = updateRequest.nickname
        person.city = updateRequest.city
        person.age = updateRequest.age
        person.avatarId = updateRequest.avatarId
        personRepo.save(person)
    }

    fun deletePerson(id: Long) {
        personRepo.deleteById(id)
    }

    fun getAllMembers(eventId: Long): ArrayList<MemberResponseEntity> {
        val event = eventRepo.findById(eventId).get()
        val members = arrayListOf<MemberResponseEntity>()
        for (person in event.members) {
            members.add(mapToMemberResponseEntity(person, event))
        }
        return members
    }

    fun getProfile(id: Long): ProfileResponseEntity {
        return mapToProfileResponseEntity(personRepo.findById(id).get())
    }

    fun getByNickname(nickname: String): Person? {
        return personRepo.findByNickname(nickname)
    }

    fun joinToEvent(userId: Long, eventId: Long) {
        val event = eventRepo.findById(eventId).get()
        val user = personRepo.findById(userId).get()
        if (!event.bannedMembers.contains(user)) {
            event.addPerson(user)
            eventRepo.save(event)
        }
    }

    fun leaveFromEvent(userId: Long, eventId: Long) {
        val user = personRepo.findById(userId).get()
        val event = eventRepo.findById(eventId).get()
        if (event.members.contains(user) && event.host.id != user.id) {
            event.kick(user)
            eventRepo.save(event)
        }
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