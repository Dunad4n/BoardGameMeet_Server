package com.example.bgm.entities

import com.fasterxml.jackson.databind.BeanDescription
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import lombok.Data
import java.sql.Date
import java.time.LocalDateTime
import java.time.ZoneOffset

@Table(name = "event")
@Data
@Entity
data class Event(

    @NotBlank
    @Column(name = "name")
    var name: String,

    @NotBlank
    @Column(name = "game")
    var game: String,

    @NotBlank
    @Column(name = "city")
    var city: String,

    @NotBlank
    @Column(name = "address")
    var address: String,

    @NotNull
    @Column(name = "date")
    var date: LocalDateTime,

    @NotNull
    @Column(name = "max_person_count")
    var maxPersonCount: Int,

    @NotNull
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "host")
    var host: Person
){

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    var id: Long? = null

    @Column(name = "min_age")
    var minAge: Int? = null

    @Column(name = "max_age")
    var maxAge: Int? = null

    @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinTable(
        name = "members_events",
        joinColumns = [JoinColumn(name = "event_id", referencedColumnName = "event_id")],
        inverseJoinColumns = [JoinColumn(name = "person_id", referencedColumnName = "person_id")]
    )
    var members = mutableListOf<Person>()

    @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinTable(
        name = "person_banned_in_events",
        joinColumns = [JoinColumn(name = "event_id", referencedColumnName = "event_id")],
        inverseJoinColumns = [JoinColumn(name = "person_id", referencedColumnName = "person_id")]
    )
    var bannedMembers = mutableListOf<Person>()

    @Column(name = "description")
    lateinit var description: String

    @Column(name = "items")
    lateinit var items: String

    @OneToMany(mappedBy = "event", cascade = [CascadeType.ALL])
    var messages = mutableListOf<Message>()

    constructor(name: String,
                game: String,
                city: String,
                address: String,
                date: LocalDateTime,
                maxPersonCount: Int,
                host: Person,
                minAge: Int,
                maxAge: Int,
                description: String)
    : this(name, game, city, address, date, maxPersonCount, host) {
        this.description = description
        this.minAge = minAge
        this.maxAge = maxAge
    }

    fun getSpace(): String { return "     " }

    fun membersForFull(): Int { return maxPersonCount - members.size }
    fun isActive(): Boolean { return LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) < date.toEpochSecond(ZoneOffset.UTC) }

    fun ban(user: Person) {
        members.remove(user)
        bannedMembers.add(user)
    }

    fun kick(user: Person) {
        members.remove(user)
    }

    fun addPerson(user: Person) {
        members.add(user)
    }

    fun getItems(): List<String> {
        return items.split("     ")
    }

}