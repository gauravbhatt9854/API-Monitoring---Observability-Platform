package com.example.demo.service

import com.example.demo.model.ApiLog
import com.example.demo.repository.logs.ApiLogRepository
import org.springframework.stereotype.Service

@Service
class LogService(private val apiLogRepository: ApiLogRepository) {
    fun save(log: ApiLog): ApiLog = apiLogRepository.save(log)
    fun listAll(): List<ApiLog> = apiLogRepository.findAll()
}
