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

    @Column(name = "age")
    var age: Int,

    @NotNull
    @Column(name = "city")
    var city: String,

    @NotNull
    @Column(name = "avatar_id")
    var avatarId: Int,

    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "people")
    var events: List<Event>,

    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "banned_people")
    var banedIn: List<Event>
)
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id = -1
        get() = field
        set(id) {
            field = id
        }
}