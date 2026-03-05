package com.taller.backend.integration

import com.taller.backend.dto.MetricsResponse
import com.taller.backend.dto.UploadRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FileApiIntegrationTest {
    
    @Autowired
    lateinit var restTemplate: TestRestTemplate
    
    @Test
    fun `full upload and download flow`() {
        // Upload
        val request = UploadRequest("test.txt", "Hello Nike")
        val uploadResponse = restTemplate.postForEntity("/files", request, Void::class.java)
        assertEquals(HttpStatus.OK, uploadResponse.statusCode)
        
        // Download
        val downloadResponse = restTemplate.getForEntity("/files/test.txt", String::class.java)
        assertEquals(HttpStatus.OK, downloadResponse.statusCode)
        assertEquals("Hello Nike", downloadResponse.body)
        
        // Metrics
        val metricsResponse = restTemplate.getForEntity("/metrics", MetricsResponse::class.java)
        assertEquals(HttpStatus.OK, metricsResponse.statusCode)
        assertTrue(metricsResponse.body!!.uploads >= 1)
        assertTrue(metricsResponse.body!!.reads >= 1)
    }
    
    @Test
    fun `should return 404 for non-existent file`() {
        val response = restTemplate.getForEntity("/files/nonexistent.txt", String::class.java)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }
    
    @Test
    fun `should return 400 for invalid filename`() {
        val request = UploadRequest("../etc/passwd", "hack")
        val response = restTemplate.postForEntity("/files", request, String::class.java)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }
}
