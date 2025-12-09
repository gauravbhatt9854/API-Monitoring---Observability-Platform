package com.example.demo.controller

import com.example.demo.model.Alert
import com.example.demo.repository.meta.AlertRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping("/api/alerts")
class AlertController(
    private val alertRepository: AlertRepository
) {

    // Get all alerts
    @GetMapping
    fun list(): List<Alert> = alertRepository.findAll()

    // Create new alert
    @PostMapping
    fun create(@RequestBody alert: Alert): Alert = alertRepository.save(alert)

    // Resolve an alert
    @PutMapping("/{id}/resolve")
    fun resolve(@PathVariable id: String): ResponseEntity<Alert> {
        val opt = alertRepository.findById(id)
        if (!opt.isPresent) return ResponseEntity.notFound().build()

        val alert = opt.get()

        // Create updated immutable copy
        val updated = alert.copy(
            resolved = true,
            resolvedAt = Instant.now()
        )

        val saved = alertRepository.save(updated)
        return ResponseEntity.ok(saved)
    }
}
