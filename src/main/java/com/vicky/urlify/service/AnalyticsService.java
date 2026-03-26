package com.vicky.urlify.service;

import com.vicky.urlify.model.ClickAnalytics;
import com.vicky.urlify.model.Url;
import com.vicky.urlify.repository.ClickAnalyticsRepository;
import com.vicky.urlify.repository.UrlRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {

    private final ClickAnalyticsRepository clickAnalyticsRepository;
    private final UrlRepository urlRepository;

    public AnalyticsService(ClickAnalyticsRepository clickAnalyticsRepository,
                            UrlRepository urlRepository) {
        this.clickAnalyticsRepository = clickAnalyticsRepository;
        this.urlRepository = urlRepository;
    }

    // @Async means this runs in a background thread
    // The redirect response goes out IMMEDIATELY
    // This DB write happens AFTER — user never waits for it
    @Async
    public void recordClick(Long urlId) {
        try {
            // Update click count on URL
            urlRepository.findById(urlId).ifPresent(url -> {
                url.setClickCount(url.getClickCount() + 1);
                urlRepository.save(url);
            });

            // Save click record
            Url url = new Url();
            url.setId(urlId);
            ClickAnalytics click = new ClickAnalytics();
            click.setUrl(url);
            clickAnalyticsRepository.save(click);

        } catch (Exception e) {
            // Analytics failure NEVER affects the redirect
            System.err.println("Analytics recording failed: " + e.getMessage());
        }
    }
}