package com.hackerrank.api.controller;

import com.hackerrank.api.model.Scan;
import com.hackerrank.api.repository.ScanRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ScanController endpoints
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ScanControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ScanRepository scanRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private Scan testScan1;
    private Scan testScan2;
    private Scan testScan3;
    
    @BeforeEach
    void setUp() {
        scanRepository.deleteAll();
        
        testScan1 = new Scan();
        testScan1.setDomainName("example.com");
        testScan1.setNumPages(100);
        testScan1.setNumBrokenLinks(5);
        testScan1.setNumMissingImages(2);
        testScan1.setDeleted(false);
        testScan1 = scanRepository.save(testScan1);
        
        testScan2 = new Scan();
        testScan2.setDomainName("example.com");
        testScan2.setNumPages(50);
        testScan2.setNumBrokenLinks(10);
        testScan2.setNumMissingImages(3);
        testScan2.setDeleted(false);
        testScan2 = scanRepository.save(testScan2);
        
        testScan3 = new Scan();
        testScan3.setDomainName("example.com");
        testScan3.setNumPages(200);
        testScan3.setNumBrokenLinks(1);
        testScan3.setNumMissingImages(5);
        testScan3.setDeleted(false);
        testScan3 = scanRepository.save(testScan3);
    }
    
    // ==================== GET /scan/{id} Tests ====================
    
    @Test
    void getScanById_WithValidId_Returns200AndScanData() throws Exception {
        mockMvc.perform(get("/scan/" + testScan1.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(testScan1.getId()))
            .andExpect(jsonPath("$.domainName").value("example.com"))
            .andExpect(jsonPath("$.numPages").value(100))
            .andExpect(jsonPath("$.numBrokenLinks").value(5))
            .andExpect(jsonPath("$.numMissingImages").value(2))
            .andExpect(jsonPath("$.deleted").value(false));
    }
    
    @Test
    void getScanById_WithInvalidId_Returns404() throws Exception {
        mockMvc.perform(get("/scan/99999"))
            .andExpect(status().isNotFound())
            .andExpect(content().string(containsString("Scan ID provided not found")));
    }
    
    // ==================== DELETE /scan/{id} Tests ====================
    
    @Test
    void deleteById_WithValidId_Returns200AndPerformsSoftDelete() throws Exception {
        Long scanId = testScan1.getId();
        
        mockMvc.perform(delete("/scan/" + scanId))
            .andExpect(status().isOk());
        
        // Verify soft delete: record exists but deleted=true
        Scan deletedScan = scanRepository.findById(scanId).orElse(null);
        assertNotNull(deletedScan, "Scan should still exist in database");
        assertTrue(deletedScan.getDeleted(), "Deleted flag should be true");
    }
    
    @Test
    void deleteById_WithInvalidId_Returns404() throws Exception {
        mockMvc.perform(delete("/scan/99999"))
            .andExpect(status().isNotFound())
            .andExpect(content().string(containsString("Scan ID provided not found")));
    }
    
    // ==================== GET /scan/search/{domainName}?orderBy={column} Tests ====================
    
    @Test
    void searchScans_WithValidOrderBy_Returns200AndSortedResults() throws Exception {
        MvcResult result = mockMvc.perform(get("/scan/search/example.com")
                .param("orderBy", "numPages"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(3)))
            .andReturn();
        
        String content = result.getResponse().getContentAsString();
        List<Scan> scans = objectMapper.readValue(content, 
            objectMapper.getTypeFactory().constructCollectionType(List.class, Scan.class));
        
        // Verify ascending order by numPages: 50, 100, 200
        assertEquals(50, scans.get(0).getNumPages());
        assertEquals(100, scans.get(1).getNumPages());
        assertEquals(200, scans.get(2).getNumPages());
    }
    
    @Test
    void searchScans_WithInvalidOrderBy_Returns400() throws Exception {
        mockMvc.perform(get("/scan/search/example.com")
                .param("orderBy", "invalidColumn"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Invalid orderBy column")));
    }
    
    @Test
    void searchScans_OrderByNumBrokenLinks_ReturnsSortedResults() throws Exception {
        MvcResult result = mockMvc.perform(get("/scan/search/example.com")
                .param("orderBy", "numBrokenLinks"))
            .andExpect(status().isOk())
            .andReturn();
        
        String content = result.getResponse().getContentAsString();
        List<Scan> scans = objectMapper.readValue(content, 
            objectMapper.getTypeFactory().constructCollectionType(List.class, Scan.class));
        
        // Verify ascending order: 1, 5, 10
        assertEquals(1, scans.get(0).getNumBrokenLinks());
        assertEquals(5, scans.get(1).getNumBrokenLinks());
        assertEquals(10, scans.get(2).getNumBrokenLinks());
    }
}
