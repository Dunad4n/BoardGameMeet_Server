package com.example.bgm.entities

import com.example.bgm.Gender
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

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private val gender: Gender,

    @Column(name = "age")
    private val age: Int,

//    @Column(name = "city")
//    private val city: Address,
//
//    @OneToMany(mappedBy = "events", cascade = [CascadeType.ALL])
//    private val events: List<Event>
)
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private val id = -1;
}