package com.taller.backend.controller

import com.taller.backend.dto.MetricsResponse
import com.taller.backend.service.MetricsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/metrics")
class MetricsController(private val metricsService: MetricsService) {
    
    @GetMapping
    fun getMetrics(): MetricsResponse {
        return metricsService.getSnapshot()
    }
}
