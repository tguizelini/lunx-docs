package com.hackerrank.api.service;

import com.hackerrank.api.model.Scan;
import java.util.List;

public interface ScanService {
    
    Scan getScanById(Long id);
    
    void deleteById(Long id);
    
    List<Scan> searchScansByDomainAndOrder(String domainName, String orderBy);
}
