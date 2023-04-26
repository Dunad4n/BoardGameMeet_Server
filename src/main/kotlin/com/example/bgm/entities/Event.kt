package com.example.bgm.entities

import jakarta.persistence.*
import java.sql.Date

@Table(name = "event")
@Entity
data class Event(

    @Column(name = "name") val name: String,

    @Column(name = "game") val game: String,

    @Column(name = "address") val address: String,

    @Column(name = "date") val date: Date,

    @Column(name = "maxPersonCount") val maxPersonCount: Int,

    @Column(name = "minAge") val minAge: Int,

    @Column(name = "maxAge") val maxAge: Int,

    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "host") val host: Person,

    @ManyToMany(mappedBy = "events", cascade = [CascadeType.ALL]) val people: List<Person>,

    @ManyToMany(mappedBy = "banedIn", cascade = [CascadeType.ALL]) val bannedPeople: List<Person>,

    @Column(name = "description") val description: String,

    @Column(name = "items") val items: String,

    @OneToMany(mappedBy = "event", cascade = [CascadeType.ALL]) val messages: List<Message>,

    @Column(name = "active") val isActive: Boolean
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private val id = -1;


}