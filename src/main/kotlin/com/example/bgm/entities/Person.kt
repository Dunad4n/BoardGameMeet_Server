package com.example.bgm.entities

import com.example.bgm.entities.enums.Gender
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "person")
data class Person(

    @NotBlank
    @Column(name = "name", nullable = false)
    var name: String,

    @NotBlank
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

    @OneToMany(mappedBy = "host", cascade = [CascadeType.ALL])
    var hostIn = mutableListOf<Event>()
}