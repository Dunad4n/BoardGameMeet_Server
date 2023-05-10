package com.example.bgm.controller.dto

import com.example.bgm.entities.enums.Gender
import java.time.LocalDateTime

/**
 * Event
 */

data class MainPageEventResponseEntity(val id: Long?,
                                       val name: String,
                                       val game: String,
                                       val address: String,
                                       val date: LocalDateTime,
                                       val curPersonCount: Int,
                                       val maxPersonCount: Int,
                                       val minAge: Int?,
                                       val maxAge: Int?)
data class MyEventsResponseEntity(val id: Long?,
                                  val name: String,
                                  val game: String,
                                  val address: String,
                                  val date: LocalDateTime,
                                  val curPersonCount: Int,
                                  val maxPersonCount: Int,
                                  val minAge: Int?,
                                  val maxAge: Int?,
                                  val host: Boolean)

data class EventResponseEntity(val id: Long?,
                               val name: String,
                               val game: String,
                               val address: String,
                               val date: LocalDateTime,
                               val curPersonCount: Int,
                               val maxPersonCount: Int,
                               val ageMin: Int?,
                               val ageMax: Int?,
                               val description: String,
                               val items: List<ItemResponseEntity>)

data class ItemResponseEntity(val name: String,
                              val marked: Boolean)

/**
 * Person
 */

data class ProfileResponseEntity(val name: String,
                                 val nickname: String,
                                 val age: Int?,
                                 val city: String,
                                 val avatarId: Long?,
                                 val gender: Gender
)

data class MemberResponseEntity(val nickname: String,
                                val avatarId: Long?,
                                val host: Boolean)

data class AuthenticationResponseEntity(val nickname: String,
                                        val token: String)

/**
 * Message
 */

data class MessageResponseEntity(val text: String,
                                 val avatarId: Long?)