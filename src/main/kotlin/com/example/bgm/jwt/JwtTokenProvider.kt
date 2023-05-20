package com.example.bgm.jwt

import com.example.bgm.entities.Person
import com.example.bgm.entities.Role
import com.example.bgm.repositories.jwt.TokenRepo
import com.example.bgm.services.PersonService
import io.jsonwebtoken.*
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.Consumer
import javax.annotation.PostConstruct


@Component
class JwtTokenProvider {

    @Value("\${jwt.token.secret}")
    lateinit var secret: String

    @Value("\${jwt.token.expired}")
    var validityInMilliseconds: Long = 0

    @Autowired
    lateinit var userDetailsService: UserDetailsService

    @Autowired
    lateinit var tokenRepo: TokenRepo

    @Autowired
    lateinit var personService: PersonService

    @PostConstruct
    protected fun init() {
        secret = Base64.getEncoder().encodeToString(secret.toByteArray())
    }

    fun createToken(username: String?, roles: List<Role>): String {
        val claims = Jwts.claims().setSubject(username)
        claims["roles"] = getRoleNames(roles)
        val now = Date()
        val validity = Date(now.time + validityInMilliseconds)
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact()
    }

    fun getAuthentication(token: String?): Authentication {
        val userDetails = userDetailsService.loadUserByUsername(getUserName(token))
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    fun getUserName(token: String?): String {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body.subject
    }

    fun resolveToken(req: HttpServletRequest): String? {
        val bearerToken = req.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer_")) {
            bearerToken.substring(7)
        } else null
    }

    fun validateToken(token: String?): Boolean {
        return try {
            val claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token)
            val person = personService.getByNickname(getUserName(token))
            !claims.body.expiration.before(Date()) && tokenRepo.findAllByPerson(person).isNotEmpty()
        } catch (e: JwtException) {
            throw JwtAuthenticationException("JWT token is expired or invalid")
        } catch (e: IllegalArgumentException) {
            throw JwtAuthenticationException("JWT token is expired or invalid")
        }
    }

    fun invalidateTokensOfUser(person: Person) {
        for (token in tokenRepo.findAllByPerson(person)) {
            tokenRepo.delete(token)
        }
    }

    private fun getRoleNames(userRoles: List<Role>): List<String> {
        val result: MutableList<String> = ArrayList()
        userRoles.forEach(Consumer { role: Role -> result.add(role.name) })
        return result
    }
}
