package com.example.bgm.jwt

import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


class JwtConfigurer(jwtTokenProvider: JwtTokenProvider)
    : SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>() {
    private val jwtTokenProvider: JwtTokenProvider

    init {
        this.jwtTokenProvider = jwtTokenProvider
    }

    override fun configure(httpSecurity: HttpSecurity) {
        val jwtTokenFilter = JwtTokenFilter(jwtTokenProvider)
        httpSecurity.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
    }
}
