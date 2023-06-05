package com.example.bgm.entities

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import lombok.NoArgsConstructor

@Entity
@Table(name = "items")
@NoArgsConstructor
data class Item (

    @NotBlank
    @Column(name = "name")
    var name: String,

    @NotNull
    @Column(name = "marked")
    var marked: Boolean

) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    var id: Long? = null

    @ManyToOne
    @JoinColumn(name = "event_id")
    lateinit var event: Event

    constructor(name: String, marked: Boolean, id: Long?, event: Event): this(name, marked) {
        this.id = id
        this.event = event
    }
}