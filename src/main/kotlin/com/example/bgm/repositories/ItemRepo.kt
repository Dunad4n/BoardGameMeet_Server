package com.example.bgm.repositories

import com.example.bgm.entities.Item
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ItemRepo: JpaRepository<Item, Long> {}