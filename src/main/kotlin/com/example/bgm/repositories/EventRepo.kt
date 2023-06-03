package com.example.bgm.repositories

import com.example.bgm.entities.Event
import com.example.bgm.entities.Person
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.sql.Date
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Repository
interface EventRepo: JpaRepository<Event, Long> {

    fun findEventById(id: Long?): Optional<Event>

    //default
    fun findAllByCityAndDateAfter(city: String, pageable: Pageable, @Param("date") date: LocalDateTime = LocalDateTime.now()): Page<Event>?
    // поиск
    fun findAllByCityAndNameContainingAndDateAfter(city: String, name: String, pageable: Pageable, @Param("date") date: LocalDateTime = LocalDateTime.now()): Page<Event>?
    // возраст
    fun findAllByCityAndNameContainingAndMinAgeLessThanEqualOrMinAgeNullAndMaxAgeGreaterThanEqualOrMaxAgeNullAndMembersNotContainingAndDateAfter(city: String, name: String, minAge: Int, maxAge: Int, pageable: Pageable, person: Person, @Param("date") date: LocalDateTime = LocalDateTime.now()): Page<Event>?
    // возраст и поиск
    fun findAllByCityAndMinAgeLessThanEqualOrMinAgeNullAndMaxAgeGreaterThanEqualOrMaxAgeNullAndMembersNotContainingAndDateAfter(city: String, minAge: Int, maxAge: Int, pageable: Pageable, person: Person, @Param("date") date: LocalDateTime = LocalDateTime.now()): Page<Event>?

    @Query("select e from Event e where e.city = :city and (e.maxAge >= :age or  e.maxAge = null) and (e.minAge <= :age or  e.minAge = null) and e.date > :date and e not in :events and size(e.members) < e.maxPersonCount order by DATE(e.date), e.maxPersonCount - size(e.members)")
    fun findAllByAge(city: String, age: Int, pageable: Pageable, events: List<Event>, date: LocalDateTime = LocalDateTime.now()): Page<Event>?

    @Query("select e from Event e where e.city = :city and (e.maxAge >= :age or  e.maxAge = null) and (e.minAge <= :age or  e.minAge = null) and e.date > :date and size(e.members) < e.maxPersonCount order by DATE(e.date), e.maxPersonCount - size(e.members)")
    fun findAllByAgeWithoutContaining(city: String, age: Int, pageable: Pageable, date: LocalDateTime = LocalDateTime.now()): Page<Event>?

    @Query("select e from Event e where e.city = :city and (e.maxAge >= :age or  e.maxAge = null) and (e.minAge <= :age or  e.minAge = null) and e.date < :date and e not in :events and size(e.members) < e.maxPersonCount and e.name like :name order by DATE(e.date), e.maxPersonCount - size(e.members)")
    fun findAllByAgeAndName(city: String, name: String, age: Int, pageable: Pageable, events: List<Event>, @Param("date") date: LocalDateTime = LocalDateTime.now()): Page<Event>?

    @Query("select e from Event e where e.city = :city and (e.maxAge >= :age or  e.maxAge = null) and (e.minAge <= :age or  e.minAge = null) and e.date < :date and size(e.members) < e.maxPersonCount and e.name like :name order by DATE(e.date), e.maxPersonCount - size(e.members)")
    fun findAllByAgeAndNameWithoutContaining(city: String, name: String, age: Int, pageable: Pageable, @Param("date") date: LocalDateTime = LocalDateTime.now()): Page<Event>?




//    @Query("select e from Event e where e.city = :city and (e.maxAge >= :age or e.maxAge = null) and (e.minAge <= :age or e.minAge = null) and not :person in e.members")
//    fun findAll(city: String, age: Int, pageable: Pageable, person: Person): Page<Event>?

//    @Query("select e from Event e where not ?1 in e.members")
//    fun findAll(person: Person, pageable: Pageable): Page<Event>?

//    @Query("select e from Event e where e not in :events")
//    fun findAll(events: List<Event>, pageable: Pageable): Page<Event>?

    fun findAllByMembersContains(person: Person, pageable: Pageable): Page<Event>?
}