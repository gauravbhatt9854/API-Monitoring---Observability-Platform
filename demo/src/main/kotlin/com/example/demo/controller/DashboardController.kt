package com.example.demo.controller

import com.example.demo.service.DashboardService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping("/api/dashboard")
class DashboardController(
    private val dashboardService: DashboardService
) {

    @GetMapping("/stats")
    fun stats(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) since: Instant?): ResponseEntity<Any> {
        val stats = dashboardService.getStats(since)
        return ResponseEntity.ok(stats)
    }

    @GetMapping("/error-rate")
    fun errorRate(@RequestParam(required = false, defaultValue = "60") minutes: Int): ResponseEntity<Any> {
        val series = dashboardService.errorRateSeries(minutes)
        return ResponseEntity.ok(series)
    }
}
