package com.example.demo.interceptor

import com.example.demo.model.ApiLog
import com.example.demo.model.RateLimitHit
import com.example.demo.repository.logs.ApiLogRepository
import com.example.demo.repository.logs.RateLimitHitRepository
import com.example.demo.service.AlertEngine
import com.example.demo.service.RateLimitService
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.time.Instant
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

/**
 * Full tracking interceptor: measures latency, request/response sizes, service header, rate-limit detection.
 * Expects caller service to send header X-Service-Name (fallback to 'unknown' if missing).
 */
@Component
class ApiTrackingInterceptor(
    private val apiLogRepository: ApiLogRepository,
    private val rateLimitHitRepository: RateLimitHitRepository,
    private val rateLimitService: RateLimitService,
    private val alertEngine: AlertEngine
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        request.setAttribute("startTime", System.nanoTime())
        // store request size if available
        request.setAttribute("requestSize", request.contentLengthLong.takeIf { it >= 0 } ?: 0L)

        val service = request.getHeader("X-Service-Name") ?: "unknown"

        // Rate limit check
        val (allowed, currentCount) = rateLimitService.allowRequest(service)
        if (!allowed) {
            // persist a rate limit hit in logs DB
            rateLimitHitRepository.save(
                RateLimitHit(
                    id = null,
                    timestamp = Instant.now(),
                    service = service,
                    meta = mapOf("path" to request.requestURI, "count" to currentCount)
                )
            )
            // create a rate-limit alert
            alertEngine.handleRateLimitHit(service, request.requestURI, currentCount)
            // do NOT block request - requirement said request should continue normally
        }
        return true
    }

    override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?) {
        val start = request.getAttribute("startTime") as? Long ?: System.nanoTime()
        val durationNs = System.nanoTime() - start
        val latencyMs = durationNs / 1_000_000

        val service = request.getHeader("X-Service-Name") ?: "unknown"
        val reqSize = request.getAttribute("requestSize") as? Long ?: (request.contentLengthLong.takeIf { it >= 0 } ?: 0L)
        val respSize = response.getHeader("Content-Length")?.toLongOrNull() ?: 0L

        val log = ApiLog(
            id = null,
            timestamp = Instant.now(),
            serviceName = service,
            method = request.method,
            path = request.requestURI,
            query = request.queryString ?: "",
            status = response.status,
            latencyMs = latencyMs,
            requestSize = reqSize,
            responseSize = respSize,
            clientIp = request.getHeader("X-Forwarded-For")?.split(",")?.first()?.trim() ?: request.remoteAddr,
            userAgent = request.getHeader("User-Agent") ?: ""
        )

        apiLogRepository.save(log)

        // Trigger alerts for latency/status (also done in CollectorController, but double-safe here)
        alertEngine.processLogForAlerts(log)
    }
}
