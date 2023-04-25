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

    @Column(name = "secret_word")
    private val secretWord: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private val gender: Gender,

    @Column(name = "age")
    private val age: Int,

    @Column(name = "city")
    private val city: String,

//    @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL])
    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "people")
    private val events: List<Event>,

    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "banned_people")
    private val banedIn: List<Event>
)
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private val id = -1;
}