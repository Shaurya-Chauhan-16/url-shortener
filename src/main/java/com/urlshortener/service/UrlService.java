package com.urlshortener.service;

import com.urlshortener.model.UrlMapping;
import com.urlshortener.repository.UrlRepository;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.security.SecureRandom;
import java.util.NoSuchElementException;

@Service
public class UrlService {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 7;
    private static final int MAX_ATTEMPTS = 5;
    private static final int MAX_URL_LENGTH = 2048;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UrlRepository repository;

    public UrlService(UrlRepository repository) {
        this.repository = repository;
    }

    private void validateUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL is required");
        }
        if (url.length() > MAX_URL_LENGTH) {
            throw new IllegalArgumentException("URL must be at most 2048 characters");
        }

        URI uri;
        try {
            uri = URI.create(url);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("URL is not valid");
        }

        String scheme = uri.getScheme();
        if (scheme == null || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))) {
            throw new IllegalArgumentException("URL must start with http:// or https://");
        }
        if (uri.getHost() == null || uri.getHost().isBlank()) {
            throw new IllegalArgumentException("URL must contain a host");
        }
    }

    private String generateUniqueCode() {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            StringBuilder code = new StringBuilder(CODE_LENGTH);
            for (int index = 0; index < CODE_LENGTH; index++) {
                code.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
            }
            String generatedCode = code.toString();
            if (!repository.existsByShortCode(generatedCode)) {
                return generatedCode;
            }
        }
        throw new IllegalStateException("Could not generate a unique short code");
    }

    public UrlMapping create(String originalUrl) {
        String trimmedUrl = originalUrl == null ? null : originalUrl.trim();
        validateUrl(trimmedUrl);
        String code = generateUniqueCode();
        repository.save(code, trimmedUrl);
        return repository.findByShortCode(code).orElseThrow(IllegalStateException::new);
    }

    public String resolve(String shortCode) {
        UrlMapping mapping = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new NoSuchElementException("Short code not found: " + shortCode));
        repository.incrementClickCount(shortCode);
        return mapping.getOriginalUrl();
    }
    
    public UrlMapping getStats(String shortCode) {
        return repository.findByShortCode(shortCode)
                .orElseThrow(() -> new NoSuchElementException("Short code not found: " + shortCode));
    }
}
