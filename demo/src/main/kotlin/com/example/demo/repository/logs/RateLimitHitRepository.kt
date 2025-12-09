package com.example.demo.repository.logs

import com.example.demo.model.RateLimitHit
import org.springframework.data.mongodb.repository.MongoRepository

interface RateLimitHitRepository : MongoRepository<RateLimitHit, String>
