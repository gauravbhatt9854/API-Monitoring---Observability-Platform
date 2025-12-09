package com.example.demo.config

import com.example.demo.filter.JwtFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfig(
    private val jwtFilter: JwtFilter
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {

        http.csrf { it.disable() }

        http.sessionManagement {
            it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }

        http.authorizeHttpRequests {
            it.requestMatchers(
                "/api/auth/login",
                "/actuator/health",
                "/collector/log"
            ).permitAll()

            it.requestMatchers(
                "/api/logs/**",
                "/api/dashboard/**",
                "/api/alerts/**",
                "/api/incidents/**"
            ).authenticated()

            it.anyRequest().permitAll()
        }

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}