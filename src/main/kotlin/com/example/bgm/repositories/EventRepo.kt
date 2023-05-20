package com.example.bgm.repositories

import com.example.bgm.entities.Event
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface EventRepo: JpaRepository<Event, Long> {

    fun findAllByCity(city: String): List<Event>?
    fun findAllByCityAndName(city: String, name: String): List<Event>?
    fun findById(id: Long?): Optional<Event>
    fun existsById(id: Long?): Boolean
}