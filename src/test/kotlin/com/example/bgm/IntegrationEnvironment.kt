package com.example.bgm

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.lifecycle.Startables


@Testcontainers
abstract class IntegrationEnvironment {


    companion object {
        var BGM_TEST_DB: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:15-alpine")
            .withDatabaseName("bgm")
            .withUsername("root")
            .withPassword("root")

        init {
            BGM_TEST_DB.start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun jdbcProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { BGM_TEST_DB.jdbcUrl }
            registry.add("spring.datasource.username") { BGM_TEST_DB.username }
            registry.add("spring.datasource.password") { BGM_TEST_DB.password }
            Startables.deepStart(BGM_TEST_DB)
        }
    }

}