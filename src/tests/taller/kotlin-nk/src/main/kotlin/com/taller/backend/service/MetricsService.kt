package com.taller.backend.service

import com.taller.backend.dto.MetricsResponse
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicLong

@Service
class MetricsService {
    private val uploads = AtomicLong(0)
    private val reads = AtomicLong(0)
    private val errors = AtomicLong(0)
    
    fun incrementUploads() {
        uploads.incrementAndGet()
    }
    
    fun incrementReads() {
        reads.incrementAndGet()
    }
    
    fun incrementErrors() {
        errors.incrementAndGet()
    }
    
    fun getSnapshot(): MetricsResponse {
        return MetricsResponse(
            uploads = uploads.get(),
            reads = reads.get(),
            errors = errors.get()
        )
    }
}
