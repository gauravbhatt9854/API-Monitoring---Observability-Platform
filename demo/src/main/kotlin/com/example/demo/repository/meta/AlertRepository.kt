package com.example.demo.repository.meta

import com.example.demo.model.Alert
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface AlertRepository : MongoRepository<Alert, String> {
    fun findByResolved(resolved: Boolean): List<Alert>
}
