package com.example.demo.config

import com.example.demo.model.User
import com.example.demo.repository.meta.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class UserSeeder {

    @Bean
    fun initAdmin(userRepo: UserRepository, encoder: PasswordEncoder) = CommandLineRunner {

        if (userRepo.findByUsername("admin") == null) {
            val admin = User(
                username = "admin",
                passwordHash = encoder.encode("admin123"),
                roles = listOf("ADMIN"),
                active = true
            )

            userRepo.save(admin)
            println("✔ Admin created: admin / admin123")
        }
    }
}