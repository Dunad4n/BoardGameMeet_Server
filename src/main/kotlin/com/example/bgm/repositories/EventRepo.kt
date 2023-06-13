package com.example.bgm.repositories

import com.example.bgm.entities.Event
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

    fun findEventById(id: Long?): Optional<Event>

    fun findAllByCityAndDateAfter(city: String, pageable: Pageable, @Param("date") date: LocalDateTime = LocalDateTime.now()): Page<Event>?

    fun findAllByCityAndNameContainingAndDateAfter(city: String, name: String, pageable: Pageable, @Param("date") date: LocalDateTime = LocalDateTime.now()): Page<Event>?

    @Query("SELECT e FROM Event e " +
            "WHERE e.city = :city " +
            "AND (e.maxAge >= :age OR e.maxAge IS NULL) " +
            "AND (e.minAge <= :age OR e.minAge IS NULL) " +
            "AND e.date > current_date " +
            "AND (:size = 0 OR e NOT IN :events) " +
            "AND SIZE(e.members) < e.maxPersonCount " +
            "ORDER BY DATE(e.date), e.maxPersonCount - SIZE(e.members)")
    fun findAllByAge(
        city: String,
        age: Int,
        pageable: Pageable,
        events: List<Event>,
        size: Int = events.size,
    ): Page<Event>?

    @Query("SELECT e FROM Event e " +
            "WHERE e.city = :city " +
            "AND (e.maxAge >= :age OR e.maxAge IS NULL) " +
            "AND (e.minAge <= :age OR e.minAge IS NULL) " +
            "AND e.date > :date " +
            "AND e.date < current_date " +
            "AND (:size = 0 OR e NOT IN :events) " +
            "AND SIZE(e.members) < e.maxPersonCount " +
            "AND e.name LIKE %:name% " +
            "ORDER BY DATE(e.date), e.maxPersonCount - SIZE(e.members)")
    fun findAllByAgeAndName(
        city: String,
        name: String,
        age: Int,
        pageable: Pageable,
        events: List<Event>,
        size: Int = events.size,
    ): Page<Event>?

    @Query("SELECT e FROM Event e WHERE e in :events AND e.date < current_date ORDER BY e.date DESC")
    fun findPastEvents(events: List<Event>): List<Event>

    @Query("SELECT e FROM Event e WHERE e in :events AND e.date >= current_date ORDER BY e.date ASC")
    fun findFutureEvents(events: List<Event>): List<Event>


}