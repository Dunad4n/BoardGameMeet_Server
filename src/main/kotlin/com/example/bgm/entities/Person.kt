package com.example.bgm.entities

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "person")
data class Person(

    @NotNull
    @Column(name = "name", nullable = false)
    var name: String,

    @NotNull
    @Column(name = "nickname", nullable = false)
    var nickname: String,

    @NotNull
    @Column(name = "password", nullable = false)
    var password: String,

    @NotNull
    @Column(name = "secret_word", nullable = false)
    var secretWord: String,

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    var gender: Gender,

    @NotNull
    @Column(name = "city", nullable = false)
    var city: String,
)
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    var id: Long? = null

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "members_events",
        joinColumns = [JoinColumn(name = "person_id", referencedColumnName = "person_id")],
        inverseJoinColumns = [JoinColumn(name = "event_id", referencedColumnName = "event_id")]
    )
    var events = mutableListOf<Event>()

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "person_banned_in_events",
        joinColumns = [JoinColumn(name = "person_id", referencedColumnName = "person_id")],
        inverseJoinColumns = [JoinColumn(name = "event_id", referencedColumnName = "event_id")]
    )
    var banedIn = mutableListOf<Event>()

    @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL])
    var messages = mutableListOf<Message>()

    @Column(name = "avatar_id")
    var avatarId: Long? = null

    @Column(name = "age")
    var age: Int? = null

}