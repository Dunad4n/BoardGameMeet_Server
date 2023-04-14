package com.example.bgm.entities

import jakarta.persistence.*

@Entity
@Table(name = "person")
data class Person(

    @Column(name = "name")
    private val name: String,

    @Column(name = "nickname")
    private val nickname: String,

    @Column(name = "password")
    private val password: String,

    @Column(name = "gender")
    private val gender: Char,

    @Column(name = "age")
    private val age: Int,

//    @Column("city")
//    private val city: Address,
)
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private val id = -1;

    constructor() : this("", "", "", 'm', -1)
}