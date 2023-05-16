package com.example.bgm.entities

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

@Table(name = "message")
@Entity
data class Message(

    @NotBlank
    @Column(name = "text")
    var text: String,

    @NotNull
    @Column(name = "date_time")
    var dateTime: LocalDateTime
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    var id: Long? = null

    @NotNull
    @ManyToOne
    @JoinColumn(name = "event")
    lateinit var event: Event

    @NotNull
    @ManyToOne
    @JoinColumn(name = "person_id")
    lateinit var person: Person

    constructor(text: String, dateTime: LocalDateTime, person: Person) : this(text, dateTime) {
        this.person = person
    }
}