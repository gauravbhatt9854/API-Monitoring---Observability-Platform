package com.example.demo.service

import com.example.demo.model.ApiLog
import com.example.demo.model.RateLimitHit
import com.example.demo.repository.logs.ApiLogRepository
import com.example.demo.repository.logs.RateLimitHitRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.Instant

data class LogSearchParams(
    val service: String? = null,
    val path: String? = null,
    val status: Int? = null,
    val slowOnly: Boolean = false,
    val brokenOnly: Boolean = false,
    val from: Instant? = null,
    val to: Instant? = null,
    val page: Int = 0,
    val size: Int = 50
)

@Service
class LogQueryService(
    private val apiLogRepository: ApiLogRepository,
    private val rateLimitHitRepository: RateLimitHitRepository
) {

    fun search(params: LogSearchParams): Page<ApiLog> {
        // naive in-memory filtering for simplicity and compatibility with small-scale assignment
        val all = apiLogRepository.findAll()
        val filtered = all.filter { log ->
            if (params.service != null && !log.serviceName.equals(params.service, ignoreCase = true)) return@filter false
            if (params.path != null && !log.path.contains(params.path, ignoreCase = true)) return@filter false
            if (params.status != null && log.status != params.status) return@filter false
            if (params.slowOnly && log.latencyMs <= 500) return@filter false
            if (params.brokenOnly && log.status < 500) return@filter false
            if (params.from != null && log.timestamp.isBefore(params.from)) return@filter false
            if (params.to != null && log.timestamp.isAfter(params.to)) return@filter false
            true
        }
        val start = params.page * params.size
        val paged = if (start >= filtered.size) listOf() else filtered.drop(start).take(params.size)
        return PageImpl(paged, PageRequest.of(params.page, params.size), filtered.size.toLong())
    }

    fun listRateLimitHits(service: String? = null, from: Instant? = null, to: Instant? = null): List<RateLimitHit> {
        val all = rateLimitHitRepository.findAll()
        return all.filter { hit ->
            if (service != null && !hit.service.equals(service, ignoreCase = true)) return@filter false
            if (from != null && hit.timestamp.isBefore(from)) return@filter false
            if (to != null && hit.timestamp.isAfter(to)) return@filter false
            true
        }
    }
}
