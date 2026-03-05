package com.hackerrank.api.controller;

import com.hackerrank.api.exception.BadRequestException;
import com.hackerrank.api.exception.ElementNotFoundException;
import com.hackerrank.api.model.Scan;
import com.hackerrank.api.service.ScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scan")
public class ScanController {
    
    @Autowired
    private ScanService scanService;
    
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Scan getScanById(@PathVariable Long id) {
        return scanService.getScanById(id);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable Long id) {
        scanService.deleteById(id);
    }
    
    @GetMapping("/search/{domainName}")
    @ResponseStatus(HttpStatus.OK)
    public List<Scan> searchScans(@PathVariable String domainName, @RequestParam String orderBy) {
        return scanService.searchScansByDomainAndOrder(domainName, orderBy);
    }
    
    @ExceptionHandler(ElementNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleElementNotFound(ElementNotFoundException e) {
        return e.getMessage();
    }
    
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(BadRequestException e) {
        return e.getMessage();
    }
}
