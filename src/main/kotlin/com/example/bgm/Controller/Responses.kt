package com.example.bgm.Controller

import com.example.bgm.entities.Gender
import java.sql.Date
import java.time.LocalDateTime

/**
 * Event
 */

data class EventsResponseEntity(val Name: String,
                                val Game: String,
                                val Address: String,
                                val Date: LocalDateTime,
                                val CurPersonCount: Int,
                                val MaxPersonCount: Int,
                                val AgeMin: Int,
                                val AgeMax: Int)
data class MyEventsResponseEntity(val Name: String,
                                  val Game: String,
                                  val Address: String,
                                  val Date: LocalDateTime,
                                  val CurPersonCount: Int,
                                  val MaxPersonCount: Int,
                                  val AgeMin: Int,
                                  val AgeMax: Int,
                                  val host: Boolean)

/**
 * Person
 */

data class ProfileResponseEntity(val Name: String,
                                 val Nickname: String,
                                 val Age: Int,
                                 val City: String,
                                 val avatarId: Long,
                                 val Gender: Gender)

data class MemberResponseEntity(val nickname: String,
                                val avatarId: Long,
                                val host: Boolean)

/**
 * Message
 */

data class MessageResponseEntity(val text: String,
                                 val avatarId: Long)