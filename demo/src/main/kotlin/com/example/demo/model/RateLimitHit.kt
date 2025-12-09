package com.example.demo.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("rate_limit_hits")
data class RateLimitHit(
    @Id val id: String? = null,
    val timestamp: Instant = Instant.now(),
    val service: String,
    val meta: Map<String, Any> = emptyMap()
)
