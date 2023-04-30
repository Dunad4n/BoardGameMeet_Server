package com.example.bgm.entities

import com.fasterxml.jackson.databind.BeanDescription
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import lombok.Data
import java.sql.Date
import java.time.LocalDateTime
import java.time.ZoneOffset

@Table(name = "event")
@Data
@Entity
data class Event(

    @NotNull
    @Column(name = "name")
    var name: String,

    @NotNull
    @Column(name = "game")
    var game: String,

    @NotNull
    @Column(name = "city")
    var city: String,

    @NotNull
    @Column(name = "address")
    var address: String,

    @NotNull
    @Column(name = "date")
    var date: LocalDateTime,

    @NotNull
    @Column(name = "max_person_count")
    var maxPersonCount: Int,

    @Column(name = "min_age")
    var minAge: Int? = null,

    @Column(name = "max_age")
    var maxAge: Int? = null,
){

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    var id: Long? = null

    @NotNull
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "host")
    lateinit var host: Person

    @ManyToMany(mappedBy = "events", cascade = [CascadeType.ALL])
    var members: ArrayList<Person> = arrayListOf()

    @ManyToMany(mappedBy = "banedIn", cascade = [CascadeType.ALL])
    var bannedMembers: ArrayList<Person> = arrayListOf()

    @Column(name = "description")
    lateinit var description: String

    @Column(name = "items")
    lateinit var items: String

    @OneToMany(mappedBy = "event", cascade = [CascadeType.ALL])
    var messages: ArrayList<Message> = arrayListOf()

    constructor(name: String,
                game: String,
                city: String,
                address: String,
                date: LocalDateTime,
                maxPersonCount: Int,
                minAge: Int,
                maxAge: Int,
                description: String,
                host: Person)
    : this(name, game, city, address, date, maxPersonCount, minAge, maxAge) {
        this.description = description
        this.host = host
    }

    fun membersForFull(): Int { return maxPersonCount - members.size }
    fun isActive(): Boolean { return LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) < date.toEpochSecond(ZoneOffset.UTC) }

    fun ban(user: Person) {
        members.remove(user)
        bannedMembers.add(user)
    }

    fun kick(user: Person) {
        members.remove(user)
    }

}