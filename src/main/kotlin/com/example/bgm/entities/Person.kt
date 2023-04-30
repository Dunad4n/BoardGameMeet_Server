package com.example.bgm.entities

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "person")
data class Person(

    @NotNull
    @Column(name = "name")
    var name: String,

    @NotNull
    @Column(name = "nickname")
    var nickname: String,

    @NotNull
    @Column(name = "password")
    var password: String,

    @NotNull
    @Column(name = "secret_word")
    var secretWord: String,

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    var gender: Gender,

    @NotNull
    @Column(name = "city")
    var city: String,
)
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null

    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "people")
    lateinit var events: ArrayList<Event>

    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "banned_people")
    lateinit var banedIn: ArrayList<Event>

    @NotNull
    @Column(name = "avatar_id")
    var avatarId: Long? = null

    @Column(name = "age")
    var age: Int? = null

}