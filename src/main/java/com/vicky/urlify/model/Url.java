package com.vicky.urlify.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "urls")
@Data
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "long_url", nullable = false, length = 2048)
    private String longUrl;

    @Column(name = "short_code", unique = true)
    private String shortCode;

    @Column(name = "custom_alias", unique = true)
    private String customAlias;

    @Column(name = "click_count")
    private Long clickCount = 0L;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "active")
    private boolean active = true;
}