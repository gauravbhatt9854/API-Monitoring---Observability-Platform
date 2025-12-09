package com.example.demo.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("alerts")
data class Alert(
    @Id val id: String? = null,
    val timestamp: Instant = Instant.now(),
    val service: String,
    val type: String,
    val message: String,
    val payload: Map<String, Any> = emptyMap(),
    val resolved: Boolean = false,
    val resolvedAt: Instant? = null
)
