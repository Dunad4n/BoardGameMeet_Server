package com.example.bgm.entities

import jakarta.persistence.*
import java.sql.Date

@Table(name = "event")
@Entity
data class Event(

    @Column(name = "name")
    private val name: String,

    @Column(name = "game")
    private val game: String,

    @Column(name = "address")
    private val address: String,

    @Column(name = "date")
    private val date: Date,

    @Column(name = "maxPersonCount")
    private val maxPersonCount: Int,

    @Column(name = "minAge")
    private val minAge: Int,

    @Column(name = "maxAge")
    private val maxAge: Int,

    @OneToOne(cascade = [CascadeType.ALL])
    @Column(name = "host")
    private val host: Person,

    @OneToMany(mappedBy = "event", cascade = [CascadeType.ALL])
    private val people: List<Person>,

    @OneToMany(mappedBy = "event", cascade = [CascadeType.ALL])
    private val bannedPeople: List<Person>,

    @Column(name = "description")
    private val description: String,

    @Column(name = "items")
    private val items: String,

    @OneToMany(mappedBy = "event", cascade = [CascadeType.ALL])
    private val messages: List<Message>,

    @Column(name = "active")
    private val isActive: Boolean
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private val id = -1;
}