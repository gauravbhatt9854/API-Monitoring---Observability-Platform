package com.example.demo.service

import com.example.demo.model.Incident
import com.example.demo.repository.meta.IncidentRepository
import org.slf4j.LoggerFactory
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class IncidentService(
    private val incidentRepository: IncidentRepository
) {
    private val log = LoggerFactory.getLogger(IncidentService::class.java)

    @Transactional(transactionManager = "metaTransactionManager")
    fun createIncident(service: String, endpoint: String, reason: String, metadata: Map<String, Any> = emptyMap()): Incident {
        val inc = Incident(
            id = null,
            createdAt = Instant.now(),
            service = service,
            endpoint = endpoint,
            reason = reason,
            status = "OPEN",
            metadata = metadata,
            version = null
        )
        return incidentRepository.save(inc)
    }

    fun getIncident(id: String): Incident? {
        val opt = incidentRepository.findById(id)
        return if (opt.isPresent) opt.get() else null
    }


    /**
     * Resolve an incident by id. Uses optimistic locking; if a conflict happens we retry few times.
     */
    fun resolveIncident(incidentId: String): Incident? {
        val maxRetries = 3
        var attempt = 0
        while (attempt < maxRetries) {
            try {
                return resolveOnce(incidentId)
            } catch (ex: OptimisticLockingFailureException) {
                attempt++
                log.warn("Optimistic lock conflict while resolving incident $incidentId, retry $attempt")
                if (attempt >= maxRetries) throw ex
                Thread.sleep(50L * attempt) // small backoff
            }
        }
        return null
    }

    @Transactional(transactionManager = "metaTransactionManager")
    protected fun resolveOnce(incidentId: String): Incident? {
        val opt = incidentRepository.findById(incidentId)
        if (!opt.isPresent) return null
        val inc = opt.get()
        if (inc.status == "RESOLVED") return inc
        inc.status = "RESOLVED"
        // updating will use @Version to ensure concurrency safety
        return incidentRepository.save(inc)
    }

    fun listOpenIncidents(): List<Incident> = incidentRepository.findByStatus("OPEN")
}
