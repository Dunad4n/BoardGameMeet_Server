package com.example.bgm.Controller

import com.example.bgm.entities.Gender
import java.time.LocalDateTime


/**
 * Event
 */

data class UpdateEventRequestEntity(val id: Long,
                                    val name: String,
                                    val game: String,
                                    val city: String,
                                    val address: String,
                                    val date: LocalDateTime,
                                    val maxPersonCount: Int,
                                    val maxAge: Int,
                                    val minAge: Int,
                                    val description: String)

/**
 * Person
 */

data class UpdatePersonRequestEntity(val id: Long,
                                     val name: String,
                                     val nickname: String,
                                     val city: String,
                                     val age: Int,
                                     val gender: Gender,
                                     val avatarId: Long)