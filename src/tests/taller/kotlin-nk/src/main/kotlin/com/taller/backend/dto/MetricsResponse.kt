package com.taller.backend.dto

data class MetricsResponse(
    val uploads: Long,
    val reads: Long,
    val errors: Long
)
