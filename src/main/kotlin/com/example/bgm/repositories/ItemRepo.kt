package com.example.bgm.repositories

import com.example.bgm.entities.Event
import com.example.bgm.entities.Item
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ItemRepo: JpaRepository<Item, Long> {

    fun findAllByEvent(event: Event, pageable: Pageable): Page<Item>
    fun findAllByEvent(event: Event): List<Item>
    fun deleteAllByEvent(event: Event)
}