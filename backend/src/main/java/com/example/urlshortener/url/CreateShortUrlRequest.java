package com.example.urlshortener.url;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record CreateShortUrlRequest(
        @NotBlank(message = "Original URL is required")
        @URL(message = "Please enter a valid URL, including http:// or https://")
        String originalUrl
) {
}
