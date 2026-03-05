package com.taller.backend.service

import com.taller.backend.exception.InvalidFilenameException
import com.taller.backend.repository.LocalFileRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class FileServiceTest {
    private val fileRepository = mock<LocalFileRepository>()
    private val metricsService = mock<MetricsService>()
    private val fileService = FileService(fileRepository, metricsService)
    
    @Test
    fun `should reject filename with path traversal`() {
        assertThrows<InvalidFilenameException> {
            fileService.saveFile("../etc/passwd", "hack")
        }
    }
    
    @Test
    fun `should reject filename with forward slash`() {
        assertThrows<InvalidFilenameException> {
            fileService.saveFile("path/to/file.txt", "content")
        }
    }
    
    @Test
    fun `should reject empty filename`() {
        assertThrows<InvalidFilenameException> {
            fileService.saveFile("", "content")
        }
    }
    
    @Test
    fun `should accept valid filename`() {
        fileService.saveFile("test.txt", "content")
        
        verify(fileRepository).write("test.txt", "content")
        verify(metricsService).incrementUploads()
    }
    
    @Test
    fun `should increment reads on successful read`() {
        whenever(fileRepository.read("test.txt")).thenReturn("content")
        
        fileService.readFile("test.txt")
        
        verify(metricsService).incrementReads()
    }
}
