package com.vicky.urlify.controller;

import com.vicky.urlify.dto.ShortenRequest;
import com.vicky.urlify.dto.ShortenResponse;
import com.vicky.urlify.service.RateLimitService;
import com.vicky.urlify.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
public class UrlController {

    private final UrlService urlService;
    private final RateLimitService rateLimitService;

    public UrlController(UrlService urlService, RateLimitService rateLimitService) {
        this.urlService = urlService;
        this.rateLimitService = rateLimitService;
    }

    @PostMapping("/api/urls/shorten")
    public ResponseEntity<?> shorten(
            @Valid @RequestBody ShortenRequest request,
            HttpServletRequest httpRequest) {

        String ip = httpRequest.getRemoteAddr();

        if (!rateLimitService.isAllowed(ip)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "Rate limit exceeded. Max 10 requests per 60 seconds."));
        }

        return ResponseEntity.ok(urlService.shortenUrl(request));
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> redirect(@PathVariable String code) {
        String longUrl = urlService.getLongUrl(code);
        return ResponseEntity.status(302)
                .location(URI.create(longUrl))
                .build();
    }

    @DeleteMapping("/api/urls/{code}")
    public ResponseEntity<?> deactivate(@PathVariable String code) {
        urlService.deactivateUrl(code);
        return ResponseEntity.ok(Map.of("message", "URL deactivated successfully"));
    }

    @GetMapping("/api/urls/{code}")
    public ResponseEntity<ShortenResponse> getUrlInfo(@PathVariable String code) {
        return ResponseEntity.ok(urlService.getUrlInfo(code));
    }
}