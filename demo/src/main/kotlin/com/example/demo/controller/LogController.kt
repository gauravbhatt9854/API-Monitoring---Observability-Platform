package com.example.demo.controller

import com.example.demo.model.ApiLog
import com.example.demo.service.LogQueryService
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping("/api/logs")
class LogController(
    private val logQueryService: LogQueryService
) {

    @GetMapping
    fun getAll(): ResponseEntity<Page<ApiLog>> {
        val params = com.example.demo.service.LogSearchParams(page = 0, size = 100)
        return ResponseEntity.ok(logQueryService.search(params))
    }

    // Search with query parameters:
    // /api/logs/search?service=demo&path=/test&slowOnly=true&page=0&size=50
    @GetMapping("/search")
    fun search(
        @RequestParam(required = false) service: String?,
        @RequestParam(required = false) path: String?,
        @RequestParam(required = false) status: Int?,
        @RequestParam(required = false, defaultValue = "false") slowOnly: Boolean,
        @RequestParam(required = false, defaultValue = "false") brokenOnly: Boolean,
        @RequestParam(required = false) from: String?,
        @RequestParam(required = false) to: String?,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "50") size: Int
    ): ResponseEntity<Page<ApiLog>> {
        val fromInstant = from?.let { Instant.parse(it) }
        val toInstant = to?.let { Instant.parse(it) }
        val params = com.example.demo.service.LogSearchParams(
            service = service,
            path = path,
            status = status,
            slowOnly = slowOnly,
            brokenOnly = brokenOnly,
            from = fromInstant,
            to = toInstant,
            page = page,
            size = size
        )
        return ResponseEntity.ok(logQueryService.search(params))
    }

    // Rate-limit-hit listing
    @GetMapping("/rate-limit-hits")
    fun listRateLimitHits(
        @RequestParam(required = false) service: String?,
        @RequestParam(required = false) from: String?,
        @RequestParam(required = false) to: String?
    ): ResponseEntity<List<com.example.demo.model.RateLimitHit>> {
        val fromInstant = from?.let { Instant.parse(it) }
        val toInstant = to?.let { Instant.parse(it) }
        return ResponseEntity.ok(logQueryService.listRateLimitHits(service, fromInstant, toInstant))
    }
}
