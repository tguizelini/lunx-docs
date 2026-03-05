package com.taller.backend.service

import com.taller.backend.exception.FileNotFoundException
import com.taller.backend.exception.InvalidFilenameException
import com.taller.backend.repository.LocalFileRepository
import org.springframework.stereotype.Service

@Service
class FileService(
    private val fileRepository: LocalFileRepository,
    private val metricsService: MetricsService
) {
    private val filenameRegex = Regex("^[a-zA-Z0-9._-]+$")
    
    fun saveFile(filename: String, content: String) {
        validateFilename(filename)
        
        try {
            fileRepository.write(filename, content)
            metricsService.incrementUploads()
        } catch (e: Exception) {
            metricsService.incrementErrors()
            throw e
        }
    }
    
    fun readFile(filename: String): String {
        validateFilename(filename)
        
        return try {
            val content = fileRepository.read(filename)
            metricsService.incrementReads()
            content
        } catch (e: java.nio.file.NoSuchFileException) {
            metricsService.incrementErrors()
            throw FileNotFoundException("File not found: $filename")
        } catch (e: Exception) {
            metricsService.incrementErrors()
            throw e
        }
    }
    
    private fun validateFilename(filename: String) {
        if (filename.isBlank()) {
            throw InvalidFilenameException("Filename cannot be empty")
        }
        
        if (!filename.matches(filenameRegex)) {
            throw InvalidFilenameException("Invalid filename: $filename. Only alphanumeric, dots, hyphens and underscores are allowed")
        }
        
        // Additional path traversal protection
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw InvalidFilenameException("Filename cannot contain path traversal characters")
        }
    }
}
