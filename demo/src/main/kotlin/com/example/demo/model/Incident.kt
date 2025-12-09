package com.example.demo.model

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "incidents")
data class Incident(
    @Id val id: String? = null,
    val createdAt: Instant = Instant.now(),
    var service: String,
    var endpoint: String,
    var reason: String,
    var status: String = "OPEN", // OPEN / RESOLVED
    var metadata: Map<String, Any> = emptyMap(),

    @Version
    var version: Long? = null
)