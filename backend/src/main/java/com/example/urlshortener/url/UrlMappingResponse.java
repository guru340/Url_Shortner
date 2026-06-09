package com.example.urlshortener.url;

import java.time.Instant;

public record UrlMappingResponse(
        Long id,
        String originalUrl,
        String shortCode,
        String shortUrl,
        long visitCount,
        Instant createdAt
) {
    public static UrlMappingResponse fromEntity(UrlMapping mapping, String backendBaseUrl) {
        return new UrlMappingResponse(
                mapping.getId(),
                mapping.getOriginalUrl(),
                mapping.getShortCode(),
                backendBaseUrl + "/" + mapping.getShortCode(),
                mapping.getVisitCount(),
                mapping.getCreatedAt()
        );
    }
}
