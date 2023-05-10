package com.example.bgm.entities.jwt

import com.example.bgm.entities.Person
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "token")
data class Token(

    @Column(name = "value")
    val value: String,

    @NotNull
    @ManyToOne
    @JoinColumn(name = "person_id")
    val person: Person

) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    var id: Long? = null


}