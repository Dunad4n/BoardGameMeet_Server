package com.example.bgm.entities

import jakarta.persistence.*
import java.sql.Date

@Table(name = "message")
data class Message(

    @Column(name = "text")
    private val text: String,

    @ManyToOne(cascade = [CascadeType.ALL])
    @Column(name = "event")
    private val event: Event,

    @OneToOne(cascade = [CascadeType.ALL])
    @Column(name = "person")
    private val person: Person,

    private val dateTime: Date
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private val id = -1;
}