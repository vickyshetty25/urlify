package com.vicky.urlify.repository;

import com.vicky.urlify.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByShortCode(String shortCode);
    Optional<Url> findByCustomAlias(String customAlias);
    boolean existsByCustomAlias(String customAlias);
}