package com.example.urlshortener.url;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
public class UrlShortenerService {

    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int CODE_LENGTH = 7;

    private final SecureRandom random = new SecureRandom();
    private final UrlMappingRepository repository;

    public UrlShortenerService(UrlMappingRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UrlMapping createShortUrl(String originalUrl) {
        String shortCode = generateUniqueShortCode();
        UrlMapping mapping = new UrlMapping(originalUrl, shortCode);
        return repository.save(mapping);
    }

    @Transactional(readOnly = true)
    public List<UrlMapping> findRecentUrls() {
        return repository.findTop20ByOrderByCreatedAtDesc();
    }

    @Transactional
    public String findOriginalUrlAndRecordVisit(String shortCode) {
        UrlMapping mapping = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));

        mapping.recordVisit();
        return mapping.getOriginalUrl();
    }

    private String generateUniqueShortCode() {
        String code;
        do {
            code = randomCode();
        } while (repository.existsByShortCode(code));

        return code;
    }

    private String randomCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(ALPHABET.length());
            code.append(ALPHABET.charAt(index));
        }

        return code.toString();
    }
}
