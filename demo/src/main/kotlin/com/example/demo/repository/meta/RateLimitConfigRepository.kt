package com.example.demo.repository.meta

import com.example.demo.model.RateLimitConfig
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface RateLimitConfigRepository : MongoRepository<RateLimitConfig, String> {
    fun findByService(service: String): RateLimitConfig?
}
