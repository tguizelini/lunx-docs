package com.hackerrank.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Scan implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String domainName;
    
    @Column(nullable = false)
    private Integer numPages;
    
    @Column(nullable = false)
    private Integer numBrokenLinks;
    
    @Column(nullable = false)
    private Integer numMissingImages;
    
    @Column(nullable = false)
    private Boolean deleted = false;
    
    // Constructors
    public Scan() {
    }
    
    public Scan(Long id, String domainName, Integer numPages, Integer numBrokenLinks, Integer numMissingImages, Boolean deleted) {
        this.id = id;
        this.domainName = domainName;
        this.numPages = numPages;
        this.numBrokenLinks = numBrokenLinks;
        this.numMissingImages = numMissingImages;
        this.deleted = deleted;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDomainName() {
        return domainName;
    }
    
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }
    
    public Integer getNumPages() {
        return numPages;
    }
    
    public void setNumPages(Integer numPages) {
        this.numPages = numPages;
    }
    
    public Integer getNumBrokenLinks() {
        return numBrokenLinks;
    }
    
    public void setNumBrokenLinks(Integer numBrokenLinks) {
        this.numBrokenLinks = numBrokenLinks;
    }
    
    public Integer getNumMissingImages() {
        return numMissingImages;
    }
    
    public void setNumMissingImages(Integer numMissingImages) {
        this.numMissingImages = numMissingImages;
    }
    
    public Boolean getDeleted() {
        return deleted;
    }
    
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
    
    @Override
    public String toString() {
        return "Scan{" +
                "id=" + id +
                ", domainName='" + domainName + '\'' +
                ", numPages=" + numPages +
                ", numBrokenLinks=" + numBrokenLinks +
                ", numMissingImages=" + numMissingImages +
                ", deleted=" + deleted +
                '}';
    }
}
