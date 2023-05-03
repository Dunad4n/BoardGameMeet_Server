package com.example.bgm.entities

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import kotlin.math.max

@Entity
@Table(name = "person")
data class Person(

    @NotBlank
    @Size(max = 20)
    @Column(name = "name", nullable = false)
    var name: String,

    @NotBlank
    @Size(max = 30)
    @Column(name = "nickname", nullable = false)
    var nickname: String,

    @NotBlank
    @Column(name = "password", nullable = false)
    var password: String,

    @NotBlank
    @Column(name = "secret_word", nullable = false)
    var secretWord: String,

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    var gender: Gender,

    @NotBlank
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "person_roles",
        joinColumns = [ JoinColumn(name = "person_id", referencedColumnName = "person_id") ],
        inverseJoinColumns = [ JoinColumn(name = "role_id", referencedColumnName = "role_id") ])
    var roles = mutableListOf<Role>()

}