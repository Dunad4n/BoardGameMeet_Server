package com.example.bgm

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.lifecycle.Startables


@Testcontainers
abstract class IntegrationEnvironment {

    companion object {

        private val dbContainer: JdbcDatabaseContainer<*>

        init {
            dbContainer = PostgreSQLContainer("postgres:15-alpine")
                .withDatabaseName("bgm_test")
                .withUsername("user")
                .withPassword("password")
            dbContainer.start()
        }

        @DynamicPropertySource
        fun jdbcProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", dbContainer::getJdbcUrl)
            registry.add("spring.datasource.username", dbContainer::getUsername)
            registry.add("spring.datasource.password", dbContainer::getPassword)
            Startables.deepStart(dbContainer)
        }

    }

}