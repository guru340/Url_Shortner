package com.example.urlshortener.url;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class UrlController {

    private final UrlShortenerService service;
    private final String backendBaseUrl;

    public UrlController(UrlShortenerService service,
                         @Value("${app.backend-base-url}") String backendBaseUrl) {
        this.service = service;
        this.backendBaseUrl = backendBaseUrl;
    }

    @PostMapping("/api/urls")
    public ResponseEntity<UrlMappingResponse> createShortUrl(@Valid @RequestBody CreateShortUrlRequest request) {
        UrlMapping mapping = service.createShortUrl(request.originalUrl());
        UrlMappingResponse response = UrlMappingResponse.fromEntity(mapping, backendBaseUrl);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/urls")
    public List<UrlMappingResponse> getRecentUrls() {
        return service.findRecentUrls()
                .stream()
                .map(mapping -> UrlMappingResponse.fromEntity(mapping, backendBaseUrl))
                .toList();
    }

    @GetMapping("/{shortCode}")
    public void redirectToOriginalUrl(@PathVariable String shortCode, HttpServletResponse response) throws IOException {
        String originalUrl = service.findOriginalUrlAndRecordVisit(shortCode);
        response.sendRedirect(originalUrl);
    }

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleMissingShortCode(UrlNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Invalid request");

        return ResponseEntity.badRequest().body(Map.of("message", message));
    }
}
