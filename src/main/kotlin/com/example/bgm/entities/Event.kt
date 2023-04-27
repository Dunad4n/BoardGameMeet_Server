package com.example.bgm.entities

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
    @Column(name = "maxPersonCount")
    var maxPersonCount: Int,

    @Column(name = "minAge")
    var minAge: Int,

    @Column(name = "maxAge")
    var maxAge: Int,

    @NotNull
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "host")
    var host: Person,

    @ManyToMany(mappedBy = "events", cascade = [CascadeType.ALL])
    var members: List<Person>,

    @ManyToMany(mappedBy = "banedIn", cascade = [CascadeType.ALL])
    var bannedMembers: List<Person>,

    @Column(name = "description")
    var description: String,

    @Column(name = "items")
    var items: String,

    @OneToMany(mappedBy = "event", cascade = [CascadeType.ALL])
    var messages: List<Message>,
){

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null
        get() = field
        set(id) {
            field = id
        }

    fun membersForFull(): Int { return maxPersonCount - members.size }
    fun isActive(): Boolean { return LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) < date.toEpochSecond(ZoneOffset.UTC) }



}