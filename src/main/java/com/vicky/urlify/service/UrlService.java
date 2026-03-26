package com.vicky.urlify.service;

import com.vicky.urlify.dto.ShortenRequest;
import com.vicky.urlify.dto.ShortenResponse;
import com.vicky.urlify.exception.UrlNotFoundException;
import com.vicky.urlify.exception.UrlExpiredException;
import com.vicky.urlify.model.Url;
import com.vicky.urlify.repository.UrlRepository;
import com.vicky.urlify.util.Base62Util;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final Base62Util base62Util;
    private final AnalyticsService analyticsService;

    private static final String BASE_URL = "http://localhost:8080/";

    public UrlService(UrlRepository urlRepository,
                      Base62Util base62Util,
                      AnalyticsService analyticsService) {
        this.urlRepository = urlRepository;
        this.base62Util = base62Util;
        this.analyticsService = analyticsService;
    }

    public ShortenResponse shortenUrl(ShortenRequest request) {
        // Check custom alias availability
        if (request.getCustomAlias() != null && !request.getCustomAlias().isBlank()) {
            if (urlRepository.existsByCustomAlias(request.getCustomAlias())) {
                throw new RuntimeException("Custom alias already taken");
            }
        }

        // Save URL first to get the auto-generated ID
        Url url = new Url();
        url.setLongUrl(request.getLongUrl());
        url.setCustomAlias(request.getCustomAlias());
        url = urlRepository.save(url);

        // Generate short code from ID using Base62
        String shortCode = base62Util.encode(url.getId());
        url.setShortCode(shortCode);
        url = urlRepository.save(url);

        String shortUrl = BASE_URL + (url.getCustomAlias() != null
                ? url.getCustomAlias() : shortCode);

        return new ShortenResponse(
                shortUrl,
                shortCode,
                url.getLongUrl(),
                url.getClickCount(),
                url.getCreatedAt());
    }

    @Cacheable(value = "urls", key = "#code")
    public String getLongUrl(String code) {
        // Try short code first, then custom alias
        Url url = urlRepository.findByShortCode(code)
                .or(() -> urlRepository.findByCustomAlias(code))
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found: " + code));

        if (!url.isActive()) {
            throw new UrlNotFoundException("This URL has been deactivated");
        }

        if (url.getExpiresAt() != null && url.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UrlExpiredException("This URL has expired");
        }

        // Increment click count asynchronously
        analyticsService.recordClick(url.getId());

        return url.getLongUrl();
    }

    @CacheEvict(value = "urls", key = "#code")
    public void deactivateUrl(String code) {
        Url url = urlRepository.findByShortCode(code)
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found"));
        url.setActive(false);
        urlRepository.save(url);
    }
    public ShortenResponse getUrlInfo(String code) {
        Url url = urlRepository.findByShortCode(code)
                .or(() -> urlRepository.findByCustomAlias(code))
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found: " + code));

        String shortUrl = BASE_URL + (url.getCustomAlias() != null
                ? url.getCustomAlias() : url.getShortCode());

        return new ShortenResponse(
                shortUrl,
                url.getShortCode(),
                url.getLongUrl(),
                url.getClickCount(),
                url.getCreatedAt());
    }
}