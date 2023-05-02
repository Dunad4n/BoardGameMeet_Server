package com.example.bgm.controller

import com.example.bgm.entities.Gender
import java.time.LocalDateTime

/**
 * Event
 */

data class MainPageEventResponseEntity(val name: String,
                                       val game: String,
                                       val address: String,
                                       val date: LocalDateTime,
                                       val curPersonCount: Int,
                                       val maxPersonCount: Int,
                                       val minAge: Int?,
                                       val maxAge: Int?)
data class MyEventsResponseEntity(val name: String,
                                  val game: String,
                                  val address: String,
                                  val date: LocalDateTime,
                                  val curPersonCount: Int,
                                  val maxPersonCount: Int,
                                  val minAge: Int?,
                                  val maxAge: Int?,
                                  val host: Boolean)

data class EventResponseEntity(val name: String,
                               val game: String,
                               val address: String,
                               val date: LocalDateTime,
                               val curPersonCount: Int,
                               val maxPersonCount: Int,
                               val ageMin: Int?,
                               val ageMax: Int?,
                               val description: String,
                               val items: List<String>)

/**
 * Person
 */

data class ProfileResponseEntity(val name: String,
                                 val nickname: String,
                                 val age: Int?,
                                 val city: String,
                                 val avatarId: Long?,
                                 val gender: Gender)

data class MemberResponseEntity(val nickname: String,
                                val avatarId: Long?,
                                val host: Boolean)

/**
 * Message
 */

data class MessageResponseEntity(val text: String,
                                 val avatarId: Long?)