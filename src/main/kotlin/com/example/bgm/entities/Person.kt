package com.example.bgm.entities

import jakarta.persistence.*

@Entity
@Table(name = "person")
data class Person(

    @Column(name = "name") val name: String,

    @Column(name = "nickname") val nickname: String,

    @Column(name = "password") val password: String,

    @Column(name = "secret_word") val secretWord: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "gender") val gender: Gender,

    @Column(name = "age") val age: Int,

    @Column(name = "city") val city: String,

    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "people") val events: List<Event>,

    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "banned_people") val banedIn: List<Event>
)
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private val id = -1;
}