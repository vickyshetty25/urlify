package com.vicky.urlify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
public class AnalyticsResponse {
    private String shortCode;
    private String longUrl;
    private Long totalClicks;
    private LocalDateTime createdAt;
    private Map<String, Long> clicksByDate;
}