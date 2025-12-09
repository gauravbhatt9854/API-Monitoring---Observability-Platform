package com.example.demo.repository.meta

import com.example.demo.model.Incident
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface IncidentRepository : MongoRepository<Incident, String> {
    fun findByStatus(status: String): List<Incident>
}
