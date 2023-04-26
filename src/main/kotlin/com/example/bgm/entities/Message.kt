package com.example.bgm.entities

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*
import java.sql.Date

@Table(name = "message")
@Entity
data class Message(

    @Column(name = "text") val text: String,

    @ManyToOne(cascade = [CascadeType.ALL])
    @JsonBackReference
    @JoinColumn(name = "event") val event: Event,

    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "person") val person: Person,

    val dateTime: Date
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private val id = -1;
}