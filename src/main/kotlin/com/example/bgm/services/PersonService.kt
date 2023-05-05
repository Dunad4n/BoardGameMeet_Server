package com.example.bgm.services

import com.example.bgm.controller.*
import com.example.bgm.entities.Event
import com.example.bgm.entities.Person
import com.example.bgm.repositories.EventRepo
import com.example.bgm.repositories.PersonRepo
import com.example.bgm.repositories.RoleRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class PersonService {

    @Autowired
    lateinit var personRepo: PersonRepo

    @Autowired
    lateinit var eventRepo: EventRepo

    @Autowired
    lateinit var roleRepo: RoleRepo


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

    fun getProfile(nickname: String): ProfileResponseEntity {
        val person = personRepo.findByNickname(nickname)
        if(person != null) {
            return mapToProfileResponseEntity(person)
        }
        throw Exception("There is no user with this nickname")
    }

    fun getByNickname(nickname: String): Person? {
        return personRepo.findByNickname(nickname)
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

//    fun register(person: Person): Person? {
//        val rolePerson: Role = roleRepo.findByName("ROLE_USER")
//        val personRoles = mutableListOf<Role>()
//        personRoles.add(rolePerson)
//        person.password = passwordEncoder.encode(person.password)
//        person.roles = personRoles
//        return personRepo.save(person)
//    }
}