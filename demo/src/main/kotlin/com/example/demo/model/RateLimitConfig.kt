package com.example.demo.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("rate_limit_config")
data class RateLimitConfig(
    @Id val id: String? = null,
    val service: String,
    val limit: Int
)
