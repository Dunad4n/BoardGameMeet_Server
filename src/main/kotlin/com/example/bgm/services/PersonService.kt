package com.example.bgm.services

import com.example.bgm.controller.CreatePersonRequestEntity
import com.example.bgm.controller.MemberResponseEntity
import com.example.bgm.controller.ProfileResponseEntity
import com.example.bgm.controller.UpdatePersonRequestEntity
import com.example.bgm.entities.Event
import com.example.bgm.entities.Person
import com.example.bgm.repositories.EventRepo
import com.example.bgm.repositories.PersonRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PersonService {

    @Autowired
    lateinit var personRepo: PersonRepo

    @Autowired
    lateinit var eventRepo: EventRepo


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

    fun joinToEvent(user: Person, eventId: Long) {
        eventRepo.findById(eventId).get().members.add(user)
    }

    fun leaveFromEvent(user: Person, eventId: Long) {
        eventRepo.findById(eventId).get().kick(user)
    }

}