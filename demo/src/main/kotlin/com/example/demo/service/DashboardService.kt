package com.example.demo.service

import com.example.demo.model.ApiLog
import com.example.demo.repository.logs.ApiLogRepository
import com.example.demo.repository.logs.RateLimitHitRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

data class DashboardStats(
    val slowCount: Long,
    val brokenCount: Long,
    val rateLimitHits: Long,
    val avgLatencyMs: Double,
    val top5SlowEndpoints: List<Pair<String, Double>>, // path -> avg latency
)

@Service
class DashboardService(
    private val apiLogRepository: ApiLogRepository,
    private val rateLimitHitRepository: RateLimitHitRepository
) {

    fun getStats(since: Instant? = null): DashboardStats {
        val cutoff = since ?: Instant.now().minus(24, ChronoUnit.HOURS)
        val logs = apiLogRepository.findAll().filter { it.timestamp.isAfter(cutoff) }

        val slowCount = logs.count { it.latencyMs > 500 }.toLong()
        val brokenCount = logs.count { it.status >= 500 }.toLong()
        val rateLimitHits = rateLimitHitRepository.findAll().count { it.timestamp.isAfter(cutoff) }.toLong()
        val avgLatency = if (logs.isEmpty()) 0.0 else logs.map { it.latencyMs }.average()

        // average latency per path
        val grouped = logs.groupBy { it.path }
            .mapValues { entry -> entry.value.map { it.latencyMs }.average() }
            .entries.sortedByDescending { it.value }
            .take(5)
            .map { it.key to it.value }

        return DashboardStats(
            slowCount = slowCount,
            brokenCount = brokenCount,
            rateLimitHits = rateLimitHits,
            avgLatencyMs = avgLatency,
            top5SlowEndpoints = grouped
        )
    }

    // Optional: error-rate timeseries for last N minutes (minute buckets)
    fun errorRateSeries(minutes: Int = 60): List<Pair<Long, Double>> {
        val now = Instant.now()
        val from = now.minus(minutes.toLong(), ChronoUnit.MINUTES)
        val logs = apiLogRepository.findAll().filter { it.timestamp.isAfter(from) }
        val buckets = (0 until minutes).map { idx ->
            val bucketStart = from.plus(idx.toLong(), ChronoUnit.MINUTES)
            val bucketEnd = bucketStart.plus(1, ChronoUnit.MINUTES)
            val bucketLogs = logs.filter { it.timestamp.isAfter(bucketStart) && it.timestamp.isBefore(bucketEnd) }
            val errorRate = if (bucketLogs.isEmpty()) 0.0 else (bucketLogs.count { it.status >= 500 }.toDouble() / bucketLogs.size.toDouble()) * 100.0
            Pair(bucketStart.toEpochMilli(), errorRate)
        }
        return buckets
    }
}
