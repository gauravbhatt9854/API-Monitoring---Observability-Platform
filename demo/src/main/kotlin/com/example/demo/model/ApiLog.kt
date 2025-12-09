package com.example.demo.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "api_logs")
data class ApiLog(
    @Id val id: String? = null,
    val timestamp: Instant = Instant.now(),
    val serviceName: String = "unknown",
    val method: String,
    val path: String,
    val query: String? = null,
    val status: Int,
    val latencyMs: Long,
    val requestSize: Long = 0L,
    val responseSize: Long = 0L,
    val clientIp: String? = null,
    val userAgent: String? = null,
    val extra: Map<String, Any> = emptyMap()
)
