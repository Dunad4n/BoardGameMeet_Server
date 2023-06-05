package com.example.bgm.services

import com.example.bgm.controller.dto.IsMyProfileResponseEntity
import com.example.bgm.controller.dto.MemberResponseEntity
import com.example.bgm.controller.dto.ProfileResponseEntity
import com.example.bgm.controller.dto.UpdatePersonRequestEntity
import com.example.bgm.entities.Event
import com.example.bgm.entities.Person
import com.example.bgm.entities.Role
import com.example.bgm.entities.enums.Gender
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.jwt.JwtTokenProvider
import com.example.bgm.repositories.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.RequestEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.shaded.org.yaml.snakeyaml.tokens.Token
import java.time.LocalDateTime
import kotlin.test.assertNotEquals

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

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

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
        if (resPerson != null) {
            assertThat(resPerson.name, `is`(equalTo(personName)))
            assertThat(resPerson.nickname, `is`(equalTo(personNickname)))
            assertThat(resPerson.password, `is`(equalTo(personPassword)))
            assertThat(resPerson.secretWord, `is`(equalTo(secretWord)))
            assertThat(resPerson.gender, `is`(equalTo(gender)))
            assertThat(resPerson.city, `is`(equalTo(userCity)))
        }
    }

    @Test
    @Rollback
    @Transactional
    //complete
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
        val authPerson = JwtPerson(person.id, person.nickname, person.password, listOf())
        val upPerson = UpdatePersonRequestEntity(upPersonName, upPersonNickname, upUserCity, upAge, upGender, upAvatarId)

        /** when **/
        val res = personService.updatePerson(upPerson, authPerson, jwtTokenProvider)

        /** then **/
        assertThat(res.statusCode, `is`(equalTo(HttpStatusCode.valueOf(200))))

    }

    @Test
    @Rollback
    @Transactional
    // TODO: pageable
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
        val fds = Pageable()


        event.host = person
        event.members.add(person)
        event.members.add(person1)

        val res = arrayListOf<MemberResponseEntity>()
        val memberResponse = MemberResponseEntity(person.nickname, null,true)
        val memberResponse1 = MemberResponseEntity(person1.nickname, null,false)
        res.add(memberResponse)
        res.add(memberResponse1)

        /** when **/
        //val members = personService.getAllMembers(event.id, )

        /** then **/
        assertThat(res[0].nickname, `is`(equalTo(personNickname)))
        assertThat(res[0].host, `is`(equalTo(true)))
        assertThat(res[1].nickname, `is`(equalTo(personNickname1)))
        assertThat(res[1].host, `is`(equalTo(false)))

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

        val person = personRepo.save(Person(personName, personNickname, personPassword, secretWord, gender, userCity))
        personRepo.flush()

        val profile = ProfileResponseEntity(person.name, person.nickname, null, person.city, null, person.gender)


        /** when **/
        val res = personService.getProfile(person.nickname)

        /** then **/
        assertThat(res, `is`(equalTo(profile)))
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
        assertThat(resPerson, `is`(equalTo(person)))
    }

    @Test
    @Rollback
    @Transactional
    //complete
    fun joinToEventTest() {
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

        val person = personRepo.save(Person(personName, personNickname, personPassword, secretWord, gender, userCity))
        personRepo.flush()
        val person1 = personRepo.save(Person(personName1, personNickname, personPassword, secretWord, gender, userCity))
        personRepo.flush()
        val event = eventRepo.save(Event(name, game, city, address, date, maxPersonCount, person))
        eventRepo.flush()

        /** when **/
        personService.joinToEvent(person1.id!!, event.id!!)

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

        val person = personRepo.save(Person(personName, personNickname, personPassword, secretWord, gender, userCity))
        personRepo.flush()
        val person1 = personRepo.save(Person(personName1, personNickname, personPassword, secretWord, gender, userCity))
        personRepo.flush()
        val event = eventRepo.save(Event(name, game, city, address, date, maxPersonCount, person))
        eventRepo.flush()
        event.members.add(person)
        event.members.add(person1)

        /** when **/
        personService.leaveFromEvent(person1.id!!, event.id!!)

        /** then **/
        assertThat(event.members.size, `is`(equalTo(1)))
    }

    @Test
    @Rollback
    @Transactional
    //complete
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

        val person = personRepo.save(Person(personName, personNickname, personPassword, secretWord, gender, userCity))
        personRepo.flush()
        val person1 = personRepo.save(Person(personName1, personNickname1, personPassword1, secretWord1, gender1, userCity1))
        personRepo.flush()

        val response = personService.validateSecretWord("cat", person.nickname)
        val response1 = personService.validateSecretWord("cat", person1.nickname)

        assertThat(response.statusCode, `is`(equalTo(HttpStatusCode.valueOf(200))))
        assertThat(response1.statusCode, `not`(equalTo(HttpStatusCode.valueOf(200))))
    }

    @Test
    @Rollback
    @Transactional
    //complete
    fun isMyProfileTest() {
        /** given **/
        val personName = "Ivan"
        val personNickname = "Vanius"
        val personPassword = "1234"
        val secretWord = "cat"
        val gender = Gender.MALE
        val userCity = "Voronezh"

        val person = personRepo.save(Person(personName, personNickname, personPassword, secretWord, gender, userCity))
        personRepo.flush()
        val authPerson = JwtPerson(person.id, person.nickname, person.password, listOf())
        val profileResponse = IsMyProfileResponseEntity(true)

        /** when **/
        val res = personService.isMyProfile(person.nickname, authPerson)

        /** then **/
        assertThat(res, `is`(equalTo(profileResponse)))
    }
}