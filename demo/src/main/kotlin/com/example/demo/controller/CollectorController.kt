package com.example.demo.controller

import com.example.demo.model.ApiLog
import com.example.demo.service.AlertService
import com.example.demo.service.IncidentService
import com.example.demo.repository.logs.ApiLogRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/collector")
class CollectorController(
    private val apiLogRepository: ApiLogRepository,
    private val alertService: AlertService,
    private val incidentService: IncidentService
) {

    @PostMapping("/log")
    fun collect(@RequestBody log: ApiLog): ResponseEntity<String> {
        // save raw log to logs DB
        val saved = apiLogRepository.save(log)

        // Alert rules
        if (log.latencyMs > 500) {
            alertService.createAlert(log.serviceName, "SLOW", "Latency exceeded 500ms", mapOf("latency" to log.latencyMs, "path" to log.path))
            // create incident for slow API
            incidentService.createIncident(log.serviceName, log.path, "Latency ${log.latencyMs}ms", mapOf("logId" to (saved.id ?: "")))
        }

        if (log.status >= 500) {
            alertService.createAlert(log.serviceName, "ERROR", "Server error ${log.status}", mapOf("status" to log.status, "path" to log.path))
            incidentService.createIncident(log.serviceName, log.path, "Status ${log.status}", mapOf("logId" to (saved.id ?: "")))
        }

        return ResponseEntity.ok("ok")
    }
}
