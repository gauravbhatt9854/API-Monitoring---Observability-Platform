package com.example.demo.util

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtil {

    // 256-bit HS256 secure key
    private val key = Keys.secretKeyFor(SignatureAlgorithm.HS256)

    fun generateToken(username: String): String {
        val now = Date()
        val expiry = Date(now.time + 1000 * 60 * 60 * 10) // 10 hours

        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(key)
            .compact()
    }

    fun extractUsername(token: String): String {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body.subject
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)

            !claims.body.expiration.before(Date())
        } catch (_: Exception) {
            false
        }
    }
}
