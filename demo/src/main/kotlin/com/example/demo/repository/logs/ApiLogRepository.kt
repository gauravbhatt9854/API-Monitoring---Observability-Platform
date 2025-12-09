package com.example.demo.repository.logs

import com.example.demo.model.ApiLog
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ApiLogRepository : MongoRepository<ApiLog, String>
