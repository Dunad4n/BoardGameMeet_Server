package com.example.bgm.services

import com.example.bgm.IntegrationEnvironment
import com.example.bgm.controller.dto.CreateEventRequestEntity
import com.example.bgm.controller.dto.UpdateEventRequest
import com.example.bgm.entities.Event
import com.example.bgm.entities.Item
import com.example.bgm.entities.Person
import com.example.bgm.entities.enums.Gender
import com.example.bgm.repositories.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.shaded.org.hamcrest.Matchers.hasSize
import java.time.LocalDateTime

@SpringBootTest
class PersonServiceTest
{
    @Autowired
    private lateinit var messageRepo: MessageRepo
}