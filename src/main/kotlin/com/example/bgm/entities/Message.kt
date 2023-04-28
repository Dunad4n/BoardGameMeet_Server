package com.example.bgm.entities

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.sql.Date
import java.time.LocalDateTime

@Table(name = "message")
@Entity
data class Message(

    @NotNull
    @Column(name = "text")
    var text: String,

    @NotNull
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "event")
    var event: Event,

    @NotNull
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "person")
    var person: Person,

    @NotNull
    @Column(name = "date_time")
    var dateTime: LocalDateTime
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id = -1
}