package com.example.bgm.repositories.jwt

import com.example.bgm.entities.Person
import com.example.bgm.entities.jwt.Token
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TokenRepo: JpaRepository<Token, Long> {

    fun findAllByPerson(person: Person): List<Token>
    fun deleteAllByPerson(person: Person)

}