package com.example.demo.service

import com.example.demo.model.RateLimitConfig
import com.example.demo.repository.meta.RateLimitConfigRepository
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Per-service sliding-window limiter (1 second window). Default 100 req/sec unless overridden in DB.
 * Exposes allowRequest(service) -> Pair(allowed:Boolean, currentCount:Int)
 */
@Service
class RateLimitService(
    private val rateLimitConfigRepo: RateLimitConfigRepository
) {
    companion object {
        const val DEFAULT_LIMIT = 100
    }

    private val counters = ConcurrentHashMap<String, SlidingWindowCounter>()
    private val lock = ReentrantLock()

    fun allowRequest(service: String): Pair<Boolean, Int> {
        val limit = getLimitForService(service)
        val counter = counters.computeIfAbsent(service) { SlidingWindowCounter(limit) }
        val allowed = counter.increment(limit)
        return Pair(allowed, counter.currentCount())
    }

    fun getLimitForService(service: String): Int {
        // check DB override
        val cfg = rateLimitConfigRepo.findByService(service)
        return cfg?.limit ?: DEFAULT_LIMIT
    }

    fun overrideLimit(service: String, limit: Int): RateLimitConfig {
        val existing = rateLimitConfigRepo.findByService(service)
        val toSave = if (existing == null) {
            RateLimitConfig(id = null, service = service, limit = limit)
        } else {
            existing.copy(limit = limit)
        }
        // reset counter for service to use new limit
        counters[service] = SlidingWindowCounter(limit)
        return rateLimitConfigRepo.save(toSave)
    }
}

class SlidingWindowCounter(private var limit: Int) {
    @Volatile
    private var windowStart = System.currentTimeMillis()
    private val count = AtomicInteger(0)
    private val lock = ReentrantLock()

    /**
     * increment and return whether within limit
     */
    fun increment(limitOverride: Int): Boolean {
        lock.withLock {
            val now = System.currentTimeMillis()
            if (now - windowStart >= 1000) {
                windowStart = now
                count.set(0)
            }
            val newCount = count.incrementAndGet()
            return newCount <= limitOverride
        }
    }

    fun currentCount(): Int = count.get()
}
