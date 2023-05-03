package com.example.bgm.entities

import jakarta.persistence.*

@Entity
@Table(name = "role")
data class Role(

    @Column(name = "name")
    var name: String

) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    var id: Long? = null

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "person_roles",
        joinColumns = [ JoinColumn(name = "role_id", referencedColumnName = "role_id") ],
        inverseJoinColumns = [ JoinColumn(name = "person_id", referencedColumnName = "person_id") ])
    var users = mutableListOf<Person>()

}