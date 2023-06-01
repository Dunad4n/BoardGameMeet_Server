package com.example.bgm.services

import com.example.bgm.controller.dto.UpdatePersonRequestEntity
import com.example.bgm.entities.Event
import com.example.bgm.entities.Person
import com.example.bgm.entities.enums.Gender
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.repositories.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@SpringBootTest
class PersonServiceTest
{
    @Autowired
    private lateinit var personRepo: PersonRepo

    @Autowired
    private lateinit var eventRepo: EventRepo

    @Autowired
    private lateinit var roleRepo: RoleRepo

    @Autowired
    private lateinit var personService: PersonService

    @Test
    @Rollback
    @Transactional
    //complete
    fun getPersonByIdTest() {
        /** given **/
        val personName = "Ivan"
        val personNickname = "Vanius"
        val personPassword = "1234"
        val secretWord = "secret"
        val gender = Gender.MALE
        val userCity = "Voronezh"

        val person = personRepo.save(Person(personName, personNickname, personPassword, secretWord, gender, userCity))
        personRepo.flush()

        /** when **/
        val resPerson = personService.getPerson(person.id)

        /** then **/
        assertThat(person.name, `is`(equalTo(personName)))
        assertThat(person.nickname, `is`(equalTo(personNickname)))
        assertThat(person.password, `is`(equalTo(personPassword)))
        assertThat(person.secretWord, `is`(equalTo(secretWord)))
        assertThat(person.gender, `is`(equalTo(gender)))
        assertThat(person.city, `is`(equalTo(userCity)))
    }

    @Test
    @Rollback
    @Transactional
    //complete (уточнить у дена про обязательность возраста и аватара)
    fun updatePersonTest() {
        /** given **/
        val personName = "Ivan"
        val personNickname = "Vanius"
        val personPassword = "1234"
        val secretWord = "secret"
        val gender = Gender.MALE
        val userCity = "Voronezh"
        val age = 18
        val avatarId = 1L

        val upPersonName = "Ivan1"
        val upPersonNickname = "Vanius1"
        val upAge = 20
        val upAvatarId = 2L
        val upGender = Gender.MALE
        val upUserCity = "Voronezh1"

        val person = personRepo.save(Person(personName, personNickname, personPassword, secretWord, gender, userCity))
        personRepo.flush()
        person.age = age
        person.avatarId = avatarId
        val jwtPerson = JwtPerson(person.id, person.nickname, person.password, listOf())
        val upPerson = UpdatePersonRequestEntity(upPersonName, upPersonNickname, upUserCity, upAge, upGender, upAvatarId)

        /** when **/
        personService.updatePerson(upPerson, jwtPerson)

        /** then **/
        assertThat(person.name, `is`(equalTo(upPersonName)))
        assertThat(person.nickname, `is`(equalTo(upPersonNickname)))
        assertThat(person.city, `is`(equalTo(upUserCity)))
        assertThat(person.age, `is`(equalTo(upAge)))
        assertThat(person.avatarId, `is`(equalTo(upAvatarId)))
    }

    // TODO: delete test(exceptions)
    @Test
    @Rollback
    @Transactional
    //complete
    fun getAllMembersTest() {
        /** given **/
        val name = "event"
        val game = "game"
        val city = "Voronezh"
        val maxPersonCount = 10
        val address = "address"
        val date = LocalDateTime.now()

        val personName = "Ivan"
        val personNickname = "Vanius"
        val personPassword = "1234"
        val secretWord = "secret"
        val gender = Gender.MALE
        val userCity = "Voronezh"

        val personName1 = "Ivan1"
        val personNickname1 = "Vanius1"
        val personPassword1 = "12341"
        val secretWord1 = "secret1"
        val gender1 = Gender.MALE
        val userCity1 = "Voronezh1"

        val person = personRepo.save(Person(personName, personNickname, personPassword, secretWord, gender, userCity))
        personRepo.flush()
        val person1 = personRepo.save(Person(personName1, personNickname1, personPassword1, secretWord1, gender1, userCity1))
        personRepo.flush()
        val event = eventRepo.save(Event(name, game, city, address, date, maxPersonCount, person))
        eventRepo.flush()
        event.host = person
        event.members.add(person)
        event.members.add(person1)

        /** when **/
        val members = personService.getAllMembers(event.id)

        /** then **/
        assertThat(members[0].nickname, `is`(equalTo(personNickname)))
        assertThat(members[0].host, `is`(equalTo(true)))
        assertThat(members[1].nickname, `is`(equalTo(personNickname1)))
        assertThat(members[1].host, `is`(equalTo(false)))

    }

    @Test
    @Rollback
    @Transactional
    //complete
    fun getProfileTest() {
        /** given **/
        val personName = "Ivan"
        val personNickname = "Vanius"
        val personPassword = "1234"
        val secretWord = "secret"
        val gender = Gender.MALE
        val userCity = "Voronezh"

        val name = "event"
        val game = "game"
        val city = "Voronezh"
        val maxPersonCount = 10
        val address = "address"
        val date = LocalDateTime.now()
        val minAge = 18
        val maxAge = 25
        val description = "gogogo"

        val person = personRepo.save(Person(personName, personNickname, personPassword, secretWord, gender, userCity))
        personRepo.flush()

        val event = eventRepo.save(Event(name, game, city, address, date, maxPersonCount, person))
        eventRepo.flush()

        /** when **/
        val profile = personService.getProfile(person.nickname)

        /** then **/
        assertThat(profile.name, `is`(equalTo(personName)))
        assertThat(profile.nickname, `is`(equalTo(personNickname)))
        assertThat(profile.city, `is`(equalTo(userCity)))
        assertThat(profile.gender, `is`(equalTo(gender)))
    }

    @Test
    @Rollback
    @Transactional
    //complete
    fun getByNicknameTest() {
        /** given **/
        val personName = "Ivan"
        val personNickname = "Vanius"
        val personPassword = "1234"
        val secretWord = "secret"
        val gender = Gender.MALE
        val userCity = "Voronezh"

        val person = personRepo.save(Person(personName, personNickname, personPassword, secretWord, gender, userCity))
        personRepo.flush()

        /** when **/
        val resPerson = personService.getByNickname(person.nickname)

        /** then **/
        assertThat(resPerson.name, `is`(equalTo(personName)))
        assertThat(resPerson.nickname, `is`(equalTo(personNickname)))
        assertThat(resPerson.password, `is`(equalTo(personPassword)))
        assertThat(resPerson.secretWord, `is`(equalTo(secretWord)))
        assertThat(resPerson.gender, `is`(equalTo(gender)))
        assertThat(resPerson.city, `is`(equalTo(userCity)))
    }

    @Test
    @Rollback
    @Transactional
    // TODO: assert exception 
    fun joinToEventTest() {
        /** given **/
        val personName = "Ivan"
        val personNickname = "Vanius"
        val personPassword = "1234"
        val secretWord = "secret"
        val gender = Gender.MALE
        val userCity = "Voronezh"

        val personName1 = "Ivan1"

        val personName2 = "Ivan2"

        val name = "event"
        val game = "game"
        val city = "Voronezh"
        val maxPersonCount = 10
        val address = "address"
        val date = LocalDateTime.now()
        val minAge = 18
        val maxAge = 25
        val description = "gogogo"

        val person = personRepo.save(Person(personName, personNickname, personPassword, secretWord, gender, userCity))
        personRepo.flush()
        val person1 = personRepo.save(Person(personName1, personNickname, personPassword, secretWord, gender, userCity))
        personRepo.flush()
        val person2 = personRepo.save(Person(personName2, personNickname, personPassword, secretWord, gender, userCity))
        personRepo.flush()
        val event = eventRepo.save(Event(name, game, city, address, date, maxPersonCount, person))
        eventRepo.flush()
        //event.bannedMembers.add(person)
        //event.members.add(person2)

        /** when **/
        personService.joinToEvent(person1.id, event.id)
        //personService.joinToEvent(person1.id, event.id)
        //personService.joinToEvent(person2.id, event.id)

        /** then **/
        assertThat(event.members[0].name, `is`(equalTo(personName1)))
        assertThat(event.members[0].nickname, `is`(equalTo(personNickname)))
        assertThat(event.members[0].password, `is`(equalTo(personPassword)))
        assertThat(event.members[0].secretWord, `is`(equalTo(secretWord)))
        assertThat(event.members[0].gender, `is`(equalTo(gender)))
        assertThat(event.members[0].city, `is`(equalTo(userCity)))
    }

    @Test
    @Rollback
    @Transactional
    //complete
    fun leaveFromEventTest() {
        /** given **/
        val personName = "Ivan"
        val personNickname = "Vanius"
        val personPassword = "1234"
        val secretWord = "secret"
        val gender = Gender.MALE
        val userCity = "Voronezh"

        val personName1 = "Ivan1"

        val name = "event"
        val game = "game"
        val city = "Voronezh"
        val maxPersonCount = 10
        val address = "address"
        val date = LocalDateTime.now()
        val minAge = 18
        val maxAge = 25
        val description = "gogogo"

        val person = personRepo.save(Person(personName, personNickname, personPassword, secretWord, gender, userCity))
        personRepo.flush()
        val person1 = personRepo.save(Person(personName1, personNickname, personPassword, secretWord, gender, userCity))
        personRepo.flush()
        val event = eventRepo.save(Event(name, game, city, address, date, maxPersonCount, person))
        eventRepo.flush()
        event.members.add(person)
        event.members.add(person1)

        /** when **/
        personService.leaveFromEvent(person1.id, event.id)

        /** then **/
        assertThat(event.members.size, `is`(equalTo(1)))
    }

    @Test
    @Rollback
    @Transactional
    // TODO: как сравнивать 
    fun validateSecretWordTest() {
        /** given **/
        val personName = "Ivan"
        val personNickname = "Vanius"
        val personPassword = "1234"
        val secretWord = "cat"
        val gender = Gender.MALE
        val userCity = "Voronezh"

        val personName1 = "Ivan1"
        val personNickname1 = "Vanius1"
        val personPassword1 = "12341"
        val secretWord1 = "dog"
        val gender1 = Gender.MALE
        val userCity1 = "Voronezh"

        val authPerson = JwtPerson(1L, "Vanius", "1234", listOf())
        val authPerson1 = JwtPerson(2L, "Vanius1", "12341", listOf())
        val person = personRepo.save(Person(personName, personNickname, personPassword, secretWord, gender, userCity))
        personRepo.flush()
        val person1 = personRepo.save(Person(personName1, personNickname1, personPassword1, secretWord1, gender1, userCity1))
        personRepo.flush()

        val response = personService.validateSecretWord("cat", authPerson)
        val response1 = personService.validateSecretWord("cat", authPerson1)

        assertThat(response.body, `is`(equalTo("correct secret word")))
        assertThat(response1.body, `is`(equalTo("wrong secret word")))
    }

    @Test
    @Rollback
    @Transactional
    // TODO: спросить дена про сравнение пароля(encoder) 
    fun changePasswordTest() {
        /** given **/
        val personName = "Ivan"
        val personNickname = "Vanius"
        val personPassword = "1234"
        val secretWord = "cat"
        val gender = Gender.MALE
        val userCity = "Voronezh"

        val newPass = "newpass"
        val repeatNewPass = "newpass"

        val person = personRepo.save(Person(personName, personNickname, personPassword, secretWord, gender, userCity))
        personRepo.flush()
        val authPerson = JwtPerson(1L, "Vanius", "1234", listOf())
        val encoder = BCryptPasswordEncoder()


        /** when **/
        personService.changePassword(newPass, repeatNewPass, authPerson)
        val sdf = encoder.encode(newPass)

        /** then **/
        assertThat(person.password, `is`(equalTo(encoder.encode(newPass))))
    }
}
















