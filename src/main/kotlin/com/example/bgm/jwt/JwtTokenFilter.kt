package com.example.bgm.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean
import java.io.IOException


data class JwtTokenFilter(val jwtTokenProvider: JwtTokenProvider) : GenericFilterBean() {


    @Throws(IOException::class, ServletException::class)
    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain) {
        val token: String? = jwtTokenProvider.resolveToken(servletRequest as HttpServletRequest)
        if (token != null && jwtTokenProvider.validateToken(token)) {
            val authentication: Authentication = jwtTokenProvider.getAuthentication(token)
            if (authentication != null) {
                SecurityContextHolder.getContext().authentication = authentication
            }
        }
        filterChain.doFilter(servletRequest, servletResponse)
    }
}
