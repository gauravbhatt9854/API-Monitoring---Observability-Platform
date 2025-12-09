package com.example.demo.controller

import com.example.demo.repository.meta.UserRepository
import com.example.demo.util.JwtUtil
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

data class LoginRequest(val username: String, val password: String)

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userRepo: UserRepository,
    private val encoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) {

    @PostMapping("/login")
    fun login(@RequestBody req: LoginRequest): ResponseEntity<Any> {

        val user = userRepo.findByUsername(req.username)
            ?: return ResponseEntity.badRequest().body(mapOf("error" to "Invalid username or password"))

        if (!encoder.matches(req.password, user.passwordHash)) {
            return ResponseEntity.badRequest().body(mapOf("error" to "Invalid username or password"))
        }

        val token = jwtUtil.generateToken(user.username)

        return ResponseEntity.ok(mapOf("token" to token))
    }
}
