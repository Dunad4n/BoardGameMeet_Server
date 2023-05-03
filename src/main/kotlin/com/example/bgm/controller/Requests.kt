package com.example.bgm.controller

import com.example.bgm.entities.Gender
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime


/**
 * Event
 */

data class CreateEventRequestEntity(val name: String,
                                    val game: String,
                                    val city: String,
                                    val address: String,
                                    val date: LocalDateTime,
                                    val maxPersonCount: Int,
                                    val minAge: Int,
                                    val maxAge: Int,
                                    val description: String,
                                    val hostId: Long)

data class UpdateEventRequestEntity(val id: Long,
                                    val name: String,
                                    val game: String,
                                    val city: String,
                                    val address: String,
                                    val date: LocalDateTime,
                                    val maxPersonCount: Int,
                                    val minAge: Int,
                                    val maxAge: Int,
                                    val description: String)

data class EditItemsRequestEntity(@JsonProperty("items") val items: List<String>)

/**
 * Person
 */

data class CreatePersonRequestEntity(val name: String,
                                     val nickname: String,
                                     val password: String,
                                     val secretWord: String,
                                     val gender: Gender,
                                     val city: String)

data class UpdatePersonRequestEntity(val id: Long,
                                     val name: String,
                                     val nickname: String,
                                     val city: String,
                                     val age: Int,
                                     val gender: Gender,
                                     val avatarId: Long)

data class JoinOrLeaveEventRequestEntity(val eventId: Long,
                                         val userId: Long)

data class AuthenticationRequestEntity(val nickname: String,
                                       val password: String)

/**
 * Message
 */

data class CreateMessageRequestEntity(val text: String,
                                      val dateTime: LocalDateTime,
                                      val userid: Long)