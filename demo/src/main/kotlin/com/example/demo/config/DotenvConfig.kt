package com.example.demo.config

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.core.env.MapPropertySource

@Configuration
class DotenvConfig {

    @Autowired
    fun loadEnv(environment: ConfigurableEnvironment) {
        val dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .ignoreIfMissing()
            .load()

        val map = dotenv.entries().associate {
            it.key to it.value
        }

        val propertySource = MapPropertySource("dotenv", map)
        environment.propertySources.addFirst(propertySource)
    }
}