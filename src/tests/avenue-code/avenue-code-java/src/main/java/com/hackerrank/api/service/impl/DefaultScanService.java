package com.hackerrank.api.service.impl;

import com.hackerrank.api.exception.BadRequestException;
import com.hackerrank.api.exception.ElementNotFoundException;
import com.hackerrank.api.model.Scan;
import com.hackerrank.api.repository.ScanRepository;
import com.hackerrank.api.service.ScanService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DefaultScanService implements ScanService {
    
    @Autowired
    private ScanRepository scanRepository;
    
    public Scan getScanById(Long id) {
        var scan = scanRepository.findById(id);
        if (scan.isEmpty()) {
            throw new ElementNotFoundException("Scan ID provided not found");
        }
        return scan.get();
    }
    
    @Transactional
    public void deleteById(Long id) {
        Scan scan = getScanById(id);
        scan.setDeleted(true);
        scanRepository.save(scan);
    }
    
    public List<Scan> searchScansByDomainAndOrder(String domainName, String orderBy) {
        try {
            Scan.class.getDeclaredField(orderBy);
        } catch (NoSuchFieldException e) {
            throw new BadRequestException("Invalid orderBy column: " + orderBy);
        }
        
        return scanRepository.findByDomainName(domainName, Sort.by(orderBy));
    }
}