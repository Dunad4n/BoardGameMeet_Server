package com.example.bgm.controller.dto

import com.example.bgm.entities.enums.Gender
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime


/**
 * Event
 */

data class MainPageEventsRequestEntity(val city: String,
                                       var search: String?)

data class CreateEventRequestEntity(val name: String,
                                    val game: String,
                                    val city: String,
                                    val address: String,
                                    val date: LocalDateTime,
                                    val maxPersonCount: Int,
                                    val minAge: Int?,
                                    val maxAge: Int?,
                                    val description: String)

data class UpdateEventRequest(val id: Long,
                              val name: String,
                              val game: String,
                              val city: String,
                              val address: String,
                              val date: LocalDateTime,
                              val maxPersonCount: Int,
                              val minAge: Int,
                              val maxAge: Int,
                              val description: String)

data class EditItemsRequestEntity(val name: String,
                                  val marked: Boolean)

data class MarkItemsRequestEntity(@JsonProperty("markedStatus") val markedStatuses: List<Boolean>)

/**
 * Person
 */

data class CreatePersonRequestEntity(val name: String,
                                     val nickname: String,
                                     val password: String,
                                     val secretWord: String,
                                     val gender: Gender,
                                     val city: String)

data class UpdatePersonRequestEntity(val name: String,
                                     val nickname: String,
                                     val city: String,
                                     val age: Int,
                                     val gender: Gender,
                                     val avatarId: Long)

data class JoinOrLeaveEventRequestEntity(@JsonProperty("eventId")val eventId: Long)

data class AuthenticationRequestEntity(val nickname: String,
                                       val password: String)

data class ValidateSecretWordRequestEntity(@JsonProperty("secretWord")val secretWord: String)

data class ChangePasswordRequestEntity(val newPassword: String,
                                       val repeatNewPassword: String)

/**
 * Message
 */

data class CreateMessageRequestEntity(val text: String,
                                      val userid: Long)