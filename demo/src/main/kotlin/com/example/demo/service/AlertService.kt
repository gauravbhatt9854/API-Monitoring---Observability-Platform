package com.example.demo.service

import com.example.demo.model.Alert
import com.example.demo.repository.meta.AlertRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class AlertService(
    private val alertRepository: AlertRepository
) {

    /**
     * Create and persist an alert. This method is transactional on the metadata DB.
     */
    @Transactional(transactionManager = "metaTransactionManager")
    fun createAlert(service: String, type: String, message: String, payload: Map<String, Any> = emptyMap()): Alert {
        val alert = Alert(
            id = null,
            timestamp = Instant.now(),
            service = service,
            type = type,
            message = message,
            payload = payload,
            resolved = false
        )
        return alertRepository.save(alert)
    }

    fun listOpenAlerts(): List<Alert> = alertRepository.findByResolved(false)

    @Transactional(transactionManager = "metaTransactionManager")
    fun markResolved(alertId: String): Alert? {
        val opt = alertRepository.findById(alertId)
        if (opt.isPresent) {
            val a = opt.get()
            val updated = a.copy(resolved = true)
            return alertRepository.save(updated)
        }
        return null
    }
}
