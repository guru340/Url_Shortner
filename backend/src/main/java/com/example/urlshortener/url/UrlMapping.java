package com.example.urlshortener.url;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "url_mappings")
public class UrlMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2048)
    private String originalUrl;

    @Column(nullable = false, unique = true, length = 12)
    private String shortCode;

    @Column(nullable = false)
    private long visitCount;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected UrlMapping() {
    }

    public UrlMapping(String originalUrl, String shortCode) {
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
    }

    @PrePersist
    void setCreatedAtBeforeInsert() {
        createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public long getVisitCount() {
        return visitCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void recordVisit() {
        visitCount++;
    }
}
