package com.example.bgm.repositories

import com.example.bgm.entities.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepo: JpaRepository<Role, Long> {

    fun findByName(name: String): Role

}