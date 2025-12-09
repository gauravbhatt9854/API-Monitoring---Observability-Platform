package com.example.demo.model

data class SearchParams(
    val serviceName: String? = null,
    val endpoint: String? = null,
    val status: Int? = null,
    val fromDate: Long? = null,
    val toDate: Long? = null,
    val slowOnly: Boolean? = false,
    val brokenOnly: Boolean? = false,
    val rateLimitHits: Boolean? = false
)
