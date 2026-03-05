package com.taller.backend.controller

import com.taller.backend.dto.ErrorResponse
import com.taller.backend.exception.FileNotFoundException
import com.taller.backend.exception.InvalidFilenameException
import com.taller.backend.service.MetricsService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler(private val metricsService: MetricsService) {
    
    @ExceptionHandler(InvalidFilenameException::class)
    fun handleInvalidFilename(ex: InvalidFilenameException): ResponseEntity<ErrorResponse> {
        metricsService.incrementErrors()
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(ex.message ?: "Invalid filename"))
    }
    
    @ExceptionHandler(FileNotFoundException::class)
    fun handleFileNotFound(ex: FileNotFoundException): ResponseEntity<ErrorResponse> {
        // Error já foi incrementado no FileService
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(ex.message ?: "File not found"))
    }
    
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        metricsService.incrementErrors()
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse("Internal server error"))
    }
}
