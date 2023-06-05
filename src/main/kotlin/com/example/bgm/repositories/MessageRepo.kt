package com.example.bgm.repositories

import com.example.bgm.entities.Event
import com.example.bgm.entities.Message
import com.example.bgm.entities.Person
import org.springframework.data.domain.Page
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.data.domain.Pageable

@Repository
interface MessageRepo: JpaRepository<Message, Long> {

    fun findByPerson(person: Person) : Message


    fun findAllByEventOrderByDateTime(event: Event, pageable: Pageable) : Page<Message>

}