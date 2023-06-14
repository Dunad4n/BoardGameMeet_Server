package com.example.bgm.entities.dto

import com.example.bgm.entities.enums.Gender
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
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
                                    var minAge: Int?,
                                    var maxAge: Int?,
                                    val description: String)

data class UpdateEventRequest(val id: Long?,
                              val name: String,
                              val game: String,
                              val city: String,
                              val address: String,
                              val date: LocalDateTime,
                              val maxPersonCount: Int,
                              val minAge: Int?,
                              val maxAge: Int?,
                              val description: String)

data class EditItemsRequestEntity(val name: String,
                                  val marked: Boolean)

data class MarkItemRequestEntity(val itemId: Long,
                                 val markedStatus: Boolean)

data class KickPersonRequestEntity(val eventId: Long,
                                   @field:Schema(example = "Vanius")val userNickname: String)

/**
 * Person
 */

data class CreatePersonRequestEntity(val name: String,
                                     val nickname: String,
                                     val password: String,
                                     val secretWord: String,
                                     val gender: Gender,
                                     val city: String,
                                     val age: Int?)

data class UpdatePersonRequestEntity(val name: String,
                                     val nickname: String,
                                     val city: String,
                                     val age: Int?,
                                     val gender: Gender,
                                     val avatarId: Long)

data class JoinOrLeaveEventRequestEntity(@JsonProperty("eventId")val eventId: Long)

data class AuthenticationRequestEntity(@field:Schema(example = "Vadim")val nickname: String,
                                       @field:Schema(example = "admin")val password: String) {

    constructor(): this("", "")
}

data class ValidateSecretWordRequestEntity(@field:Schema(example = "admin")val secretWord: String,
                                           val nickname: String)

data class ChangePasswordRequestEntity(val newPassword: String,
                                       val repeatNewPassword: String,
                                       val nickname: String)

data class VerifyTokenRequestEntity(val token: String,
                                    val nickname: String)

/**
 * Message
 */

data class CreateMessageRequestEntity(val text: String,
                                      val eventId: Long,
                                      val personNickname: String)