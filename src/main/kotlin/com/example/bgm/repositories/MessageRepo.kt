package com.example.bgm.repositories

import com.example.bgm.entities.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepo: JpaRepository<Message, Long> {}