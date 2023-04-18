package com.example.bgm.entities

import jakarta.persistence.*
import java.sql.Date

@Table(name = "event")
data class Event(

    @Column(name = "name")
    private val name: String,

    @Column(name = "game")
    private val game: String,

    @Column(name = "address")
    private val address: Address,

    @Column(name = "date")
    private val date: Date,

    @Column(name = "maxPersonCount")
    private val maxPersonCount: Int,

    @Column(name = "ages")
    private val ages: AgeRange,

    @OneToOne(cascade = [CascadeType.ALL])
    @Column(name = "host")
    private val host: Person,

    @OneToMany(mappedBy = "people", cascade = [CascadeType.ALL])
    private val people: List<Person>,

    @OneToMany(mappedBy = "bannedPeople", cascade = [CascadeType.ALL])
    private val bannedPeople: List<Person>,

    @Column(name = "description")
    private val description: String,

    @Column(name = "items")
    private val items: String,

    @OneToMany(mappedBy = "messages", cascade = [CascadeType.ALL])
    private val messages: List<Message>,

    @Column(name = "active")
    private val isActive: Boolean
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private val id = -1;
}