package com.example.demo.service

import com.example.demo.model.Alert
import com.example.demo.model.ApiLog
import com.example.demo.repository.meta.AlertRepository
import org.springframework.stereotype.Service
import java.time.Instant

/**
 * Central alerting logic. Uses AlertRepository (meta DB) via AlertService or directly.
 */
@Service
class AlertEngine(
    private val alertRepository: AlertRepository
) {

    fun processLogForAlerts(log: ApiLog) {
        if (log.latencyMs > 500) {
            createAlert(log.serviceName, "SLOW", "Latency ${log.latencyMs}ms on ${log.path}", mapOf("logId" to (log.id ?: ""), "latency" to log.latencyMs))
        }
        if (log.status >= 500) {
            createAlert(log.serviceName, "ERROR", "Status ${log.status} on ${log.path}", mapOf("logId" to (log.id ?: ""), "status" to log.status))
        }
    }

    fun handleRateLimitHit(service: String, path: String, count: Int) {
        createAlert(service, "RATE_LIMIT", "Rate limit exceeded for $service (count=$count) on $path", mapOf("path" to path, "count" to count))
    }

    private fun createAlert(service: String, type: String, message: String, payload: Map<String, Any> = emptyMap()): Alert {
        val alert = Alert(
            id = null,
            timestamp = Instant.now(),
            service = service,
            type = type,
            message = message,
            payload = payload,
            resolved = false,
            resolvedAt = null
        )
        return alertRepository.save(alert)
    }
}
