package com.example.demo.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
data class User(
    @Id val id: String? = null,
    val username: String,
    val passwordHash: String,
    val roles: List<String> = listOf("USER"),
    val active: Boolean = true
)
