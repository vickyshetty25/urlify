package com.vicky.urlify.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
public class RateLimitService {

    private final RedisTemplate<String, String> redisTemplate;

    // Max 10 requests per IP per 60 seconds
    private static final int MAX_REQUESTS = 10;
    private static final int WINDOW_SECONDS = 60;

    public RateLimitService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAllowed(String ipAddress) {
        String key = "ratelimit:" + ipAddress;
        long now = Instant.now().getEpochSecond();
        long windowStart = now - WINDOW_SECONDS;

        // Remove timestamps older than the window
        redisTemplate.opsForZSet().removeRangeByScore(key, 0, windowStart);

        // Count requests in current window
        Long requestCount = redisTemplate.opsForZSet().zCard(key);

        if (requestCount != null && requestCount >= MAX_REQUESTS) {
            return false; // Rate limit exceeded
        }

        // Add current timestamp
        redisTemplate.opsForZSet().add(key, String.valueOf(now), now);

        // Set expiry on the key
        redisTemplate.expire(key, WINDOW_SECONDS + 10, TimeUnit.SECONDS);

        return true;
    }
}