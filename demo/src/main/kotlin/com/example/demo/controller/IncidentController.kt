package com.example.demo.controller

import com.example.demo.model.Incident
import com.example.demo.service.IncidentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/incidents")
class IncidentController(
    private val incidentService: IncidentService
) {

    @PostMapping("/create")
    fun createIncident(@RequestBody req: CreateIncidentRequest): ResponseEntity<Incident> {
        if (req.service.isBlank() || req.endpoint.isBlank() || req.reason.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(null)
        }

        val incident = incidentService.createIncident(
            service = req.service,
            endpoint = req.endpoint,
            reason = req.reason,
            metadata = req.metadata ?: emptyMap()
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(incident)
    }

    @PostMapping("/{id}/resolve")
    fun resolveIncident(@PathVariable id: String): ResponseEntity<Any> {
        return try {
            val resolved = incidentService.resolveIncident(id)
            if (resolved != null) ResponseEntity.ok(resolved)
            else ResponseEntity.status(HttpStatus.NOT_FOUND).body("Incident not found")
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Conflict while resolving incident. Try again.")
        }
    }

    @GetMapping("/open")
    fun getOpenIncidents(): ResponseEntity<List<Incident>> {
        return ResponseEntity.ok(incidentService.listOpenIncidents())
    }

    @GetMapping("/{id}")
    fun getIncident(@PathVariable id: String): ResponseEntity<Any> {
        val inc = incidentService.getIncident(id)
        return if (inc != null) ResponseEntity.ok(inc)
        else ResponseEntity.status(HttpStatus.NOT_FOUND).body("Incident not found")
    }
}

data class CreateIncidentRequest(
    val service: String,
    val endpoint: String,
    val reason: String,
    val metadata: Map<String, Any>? = emptyMap()
)
