package com.urlshortener.controller;

import com.urlshortener.model.UrlMapping;
import com.urlshortener.service.UrlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UrlController {
    private final UrlService service;

    public UrlController(UrlService service) {
        this.service = service;
    }

    public record CreateUrlRequest(String originalUrl) {
    }

    // Creates a shortened URL.
    @PostMapping("/api/urls")
    @ResponseStatus(HttpStatus.CREATED)
    public UrlMapping create(@RequestBody CreateUrlRequest request) {
        return service.create(request.originalUrl());
    }

    // Redirects to the original URL; 302 allows the destination to change without permanent caching.
    // Redirects a short code to its original URL.
    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        String originalUrl = service.resolve(code);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(java.net.URI.create(originalUrl))
                .build();
    }

    // Returns statistics for a shortened URL.
    @GetMapping("/api/urls/{code}/stats")
    public UrlMapping stats(@PathVariable String code) {
        return service.getStats(code);
    }
}
