package com.hackerrank.api.service.impl;

import com.hackerrank.api.exception.BadRequestException;
import com.hackerrank.api.exception.ElementNotFoundException;
import com.hackerrank.api.model.Scan;
import com.hackerrank.api.repository.ScanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DefaultScanService
 */
@ExtendWith(MockitoExtension.class)
public class DefaultScanServiceTest {
    
    @Mock
    private ScanRepository scanRepository;
    
    @InjectMocks
    private DefaultScanService scanService;
    
    private Scan testScan;
    
    @BeforeEach
    void setUp() {
        testScan = new Scan();
        testScan.setId(1L);
        testScan.setDomainName("example.com");
        testScan.setNumPages(100);
        testScan.setNumBrokenLinks(5);
        testScan.setNumMissingImages(2);
        testScan.setDeleted(false);
    }
    
    // ==================== getScanById() Tests ====================
    
    @Test
    void getScanById_WithValidId_ReturnsScan() {
        when(scanRepository.findById(1L)).thenReturn(Optional.of(testScan));
        
        Scan result = scanService.getScanById(1L);
        
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("example.com", result.getDomainName());
        verify(scanRepository, times(1)).findById(1L);
    }
    
    @Test
    void getScanById_WithInvalidId_ThrowsElementNotFoundException() {
        when(scanRepository.findById(99L)).thenReturn(Optional.empty());
        
        ElementNotFoundException exception = assertThrows(
            ElementNotFoundException.class,
            () -> scanService.getScanById(99L)
        );
        
        assertEquals("Scan ID provided not found", exception.getMessage());
        verify(scanRepository, times(1)).findById(99L);
    }
    
    // ==================== deleteById() Tests ====================
    
    @Test
    void deleteById_WithValidId_PerformsSoftDelete() {
        when(scanRepository.findById(1L)).thenReturn(Optional.of(testScan));
        when(scanRepository.save(any(Scan.class))).thenReturn(testScan);
        
        scanService.deleteById(1L);
        
        assertTrue(testScan.getDeleted(), "Deleted flag should be set to true");
        verify(scanRepository, times(1)).findById(1L);
        verify(scanRepository, times(1)).save(testScan);
    }
    
    @Test
    void deleteById_WithInvalidId_ThrowsElementNotFoundException() {
        when(scanRepository.findById(99L)).thenReturn(Optional.empty());
        
        ElementNotFoundException exception = assertThrows(
            ElementNotFoundException.class,
            () -> scanService.deleteById(99L)
        );
        
        assertEquals("Scan ID provided not found", exception.getMessage());
        verify(scanRepository, times(1)).findById(99L);
        verify(scanRepository, never()).save(any(Scan.class));
    }
    
    // ==================== searchScansByDomainAndOrder() Tests ====================
    
    @Test
    void searchScansByDomainAndOrder_WithValidColumn_ReturnsOrderedScans() {
        Scan scan1 = new Scan();
        scan1.setDomainName("example.com");
        scan1.setNumPages(50);
        
        Scan scan2 = new Scan();
        scan2.setDomainName("example.com");
        scan2.setNumPages(100);
        
        List<Scan> expectedScans = Arrays.asList(scan1, scan2);
        
        when(scanRepository.findByDomainName("example.com", Sort.by("numPages")))
            .thenReturn(expectedScans);
        
        List<Scan> result = scanService.searchScansByDomainAndOrder("example.com", "numPages");
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(50, result.get(0).getNumPages());
        assertEquals(100, result.get(1).getNumPages());
        verify(scanRepository, times(1)).findByDomainName("example.com", Sort.by("numPages"));
    }
    
    @Test
    void searchScansByDomainAndOrder_WithInvalidColumn_ThrowsBadRequestException() {
        BadRequestException exception = assertThrows(
            BadRequestException.class,
            () -> scanService.searchScansByDomainAndOrder("example.com", "invalidColumn")
        );
        
        assertTrue(exception.getMessage().contains("Invalid orderBy column"));
        verify(scanRepository, never()).findByDomainName(anyString(), any(Sort.class));
    }
    
    @Test
    void searchScansByDomainAndOrder_WithAllValidColumns_DoesNotThrowException() {
        String[] validColumns = {"id", "domainName", "numPages", "numBrokenLinks", "numMissingImages", "deleted"};
        
        when(scanRepository.findByDomainName(anyString(), any(Sort.class)))
            .thenReturn(Arrays.asList(testScan));
        
        for (String column : validColumns) {
            assertDoesNotThrow(
                () -> scanService.searchScansByDomainAndOrder("example.com", column),
                "Column " + column + " should be valid"
            );
        }
        
        verify(scanRepository, times(validColumns.length))
            .findByDomainName(anyString(), any(Sort.class));
    }
}
