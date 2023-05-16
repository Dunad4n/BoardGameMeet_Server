package com.example.bgm.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import lombok.RequiredArgsConstructor
import org.springframework.boot.info.BuildProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


//@RequiredArgsConstructor
//@Configuration
//class OpenApiConfig {
//    fun customOpenAPI(): OpenAPI {
//        return OpenAPI()
//            .info(
//                Info()
//                    .termsOfService("https://springdoc.org/%22)")
//                    .components(
//                        Components()
//                            .addSecuritySchemes(OAUTH2_CLIENT_CREDENTIALS_KEY, clientCredentialSecurityScheme())
//                            .addSecuritySchemes(BEARER_TOKEN_KEY, bearerTokenSecurityScheme())
//                    )
//                    .addSecurityItem(SecurityRequirement().addList(OAUTH2_CLIENT_CREDENTIALS_KEY))
//                    .addSecurityItem(SecurityRequirement().addList(BEARER_TOKEN_KEY))
//    }
//}