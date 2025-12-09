package com.example.demo.filter

import com.example.demo.util.JwtUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(
    private val jwtUtil: JwtUtil
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        req: HttpServletRequest,
        res: HttpServletResponse,
        chain: FilterChain
    ) {

        val authHeader = req.getHeader("Authorization")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7)

            val username = try {
                jwtUtil.extractUsername(token)
            } catch (e: Exception) {
                null
            }

            if (username != null &&
                SecurityContextHolder.getContext().authentication == null &&
                jwtUtil.validateToken(token)
            ) {
                val userDetails = User(username, "", emptyList())
                val auth = UsernamePasswordAuthenticationToken(userDetails, null, emptyList())
                SecurityContextHolder.getContext().authentication = auth
            }
        }

        chain.doFilter(req, res)
    }
}