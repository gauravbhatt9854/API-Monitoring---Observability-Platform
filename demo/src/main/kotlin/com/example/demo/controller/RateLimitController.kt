package com.example.demo.controller

import com.example.demo.model.RateLimitConfig
import com.example.demo.service.RateLimitService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

data class RateLimitOverrideRequest(val service: String, val limit: Int)

@RestController
@RequestMapping("/api/ratelimit")
class RateLimitController(
    private val rateLimitService: RateLimitService
) {

    @PostMapping("/override")
    fun overrideLimit(@RequestBody req: RateLimitOverrideRequest): ResponseEntity<RateLimitConfig> {
        val saved = rateLimitService.overrideLimit(req.service, req.limit)
        return ResponseEntity.ok(saved)
    }

    @GetMapping("/current")
    fun currentLimit(@RequestParam service: String): ResponseEntity<Map<String, Any>> {
        val limit = rateLimitService.getLimitForService(service)
        return ResponseEntity.ok(mapOf("service" to service, "limit" to limit))
    }
}
