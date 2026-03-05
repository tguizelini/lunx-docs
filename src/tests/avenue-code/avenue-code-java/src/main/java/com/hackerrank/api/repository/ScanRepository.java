package com.hackerrank.api.repository;

import com.hackerrank.api.model.Scan;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScanRepository extends JpaRepository<Scan, Long> {
    
    /**
     * Find all scans by domain name with sorting
     * @param domainName the domain name to search for
     * @param sort sorting specification
     * @return list of scans matching the domain name, sorted
     */
    List<Scan> findByDomainName(String domainName, Sort sort);
}
