package com.example.bgm.repositories

import com.example.bgm.entities.Event
import com.example.bgm.entities.Person
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface EventRepo: JpaRepository<Event, Long> {

    fun findById(id: Long?): Optional<Event>

    fun findAllByCity(city: String): List<Event>?
    fun findAllByCityAndDateAfterOrderByDate(city: String, pageable: Pageable, @Param("date") date: LocalDateTime = LocalDateTime.now()): Page<Event>?
    fun findAllByCityAndName(city: String, name: String): List<Event>?

    //default
    fun findAllByCityAndDateAfter(city: String, pageable: Pageable, @Param("date") date: LocalDateTime = LocalDateTime.now()): Page<Event>?
    // поиск
    fun findAllByCityAndNameContainingAndDateAfter(city: String, name: String, pageable: Pageable, @Param("date") date: LocalDateTime = LocalDateTime.now()): Page<Event>?
    // возраст
    fun findAllByCityAndNameContainingAndMinAgeLessThanEqualAndMaxAgeGreaterThanEqualAndMembersNotContainingAndDateAfter(city: String, name: String, minAge: Int, maxAge: Int, pageable: Pageable, person: Person, @Param("date") date: LocalDateTime = LocalDateTime.now()): Page<Event>?
    // возраст и поиск
    fun findAllByCityAndMinAgeLessThanEqualAndMaxAgeGreaterThanEqualAndMembersNotContainingAndDateAfter(city: String, minAge: Int, maxAge: Int, pageable: Pageable, person: Person, @Param("date") date: LocalDateTime = LocalDateTime.now()): Page<Event>?

    fun findAllByMembersContains(person: Person, pageable: Pageable): Page<Event>?

//    fun findAllWithAgeAndSearch(city: String, name: String, age: Int,  pageable: Pageable) =
//        findAllByCityAndNameContainingAndMinAgeBeforeAndMaxAgeAfterAndDateAfter(city, name, age, age, pageable)
//    fun findAllWithAge(city: String, age: Int,  pageable: Pageable) =
//        findAllByCityAndNameContainingAndMinAgeBeforeAndMaxAgeAfterAndDateAfter(city, name, age, age, pageable)
}