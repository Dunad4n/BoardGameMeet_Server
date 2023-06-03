package com.example.bgm.repositories

import com.example.bgm.entities.Event
import com.example.bgm.entities.Person
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PersonRepo: JpaRepository<Person, Long> {

    override fun findById(id: Long): Optional<Person>
    fun findByNickname(nickname: String): Person?
    fun existsByNickname(nickname: String): Boolean
    fun deleteByNickname(nickname: String)
    fun findAllByEventsContaining(event: Event, pageable: Pageable): Page<Person>
}