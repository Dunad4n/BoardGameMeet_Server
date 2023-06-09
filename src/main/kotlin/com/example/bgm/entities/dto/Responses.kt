package com.example.bgm.entities.dto

import com.example.bgm.entities.enums.Gender
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

/**
 * Event
 */
data class CreateEventResponseEntity(val id: Long?,
                                     val name: String,
                                     val game: String,
                                     val city: String,
                                     val address: String,
                                     val date: LocalDateTime,
                                     val curPersonCount: Int,
                                     val maxPersonCount: Int,
                                     val minAge: Int?,
                                     val maxAge: Int?,
                                     val description: String,
                                     val hostId: Long?)
data class MainPageEventResponseEntity(val id: Long?,
                                       val name: String,
                                       val game: String,
                                       val address: String,
                                       val date: LocalDateTime,
                                       val curPersonCount: Int,
                                       val maxPersonCount: Int,
                                       val minAge: Int?,
                                       val maxAge: Int?,
                                       val description: String?)
data class MyEventsResponseEntity(val id: Long?,
                                  val name: String,
                                  val game: String,
                                  val address: String,
                                  val date: LocalDateTime,
                                  val curPersonCount: Int,
                                  val maxPersonCount: Int,
                                  val minAge: Int?,
                                  val maxAge: Int?,
                                  val description: String?,
                                  val host: Boolean)

data class EventResponseEntity(val id: Long?,
                               val name: String,
                               val game: String,
                               val address: String,
                               val date: LocalDateTime,
                               val curPersonCount: Int,
                               val maxPersonCount: Int,
                               val minAge: Int?,
                               val maxAge: Int?,
                               val description: String,
                               val items: List<ItemResponseEntity>,
                               val host: Boolean)

data class ItemResponseEntity(val itemId: Long?,
                              val name: String,
                              val marked: Boolean)

/**
 * Person
 */

data class UpdateProfileEntity(val token: String,
                               val nickname: String)

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
                                        val token: String,
                                        val role: String)

data class IsMyProfileResponseEntity(@JsonProperty("isMyProfile")val isMyProfile: Boolean)

/**
 * Message
 */

data class MessageResponseEntity(val text: String,
                                 val eventId: Long?,
                                 val myNickname: String,
                                 val name: String,
                                 val avatarId: Long?)