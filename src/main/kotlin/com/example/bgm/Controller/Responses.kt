package com.example.bgm.Controller

import com.example.bgm.entities.Gender
import java.sql.Date

data class EventsResponseEntity(val Name: String,
                                val Game: String,
                                val Address: String,
                                val Date: Date,
                                val CurPersonCount: Int,
                                val MaxPersonCount: Int,
                                val AgeMin: Int,
                                val AgeMax: Int)
data class MyEventsResponseEntity(val Name: String,
                                  val Game: String,
                                  val Address: String,
                                  val Date: Date,
                                  val CurPersonCount: Int,
                                  val MaxPersonCount: Int,
                                  val AgeMin: Int,
                                  val AgeMax: Int,
                                  val host: Boolean)

data class ProfileResponseEntity(val Name: String,
                                 val Nickname: String,
                                 val Age: Int,
                                 val City: String,
                                 val Avatar: String,
                                 val Gender: Gender)