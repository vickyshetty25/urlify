package com.vicky.urlify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ShortenResponse {
    private String shortUrl;
    private String shortCode;
    private String longUrl;
    private Long clickCount;
    private LocalDateTime createdAt;
}