package com.vicky.urlify.repository;

import com.vicky.urlify.model.ClickAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ClickAnalyticsRepository extends JpaRepository<ClickAnalytics, Long> {

    List<ClickAnalytics> findByUrlId(Long urlId);

    @Query("SELECT COUNT(c) FROM ClickAnalytics c WHERE c.url.id = :urlId")
    Long countByUrlId(@Param("urlId") Long urlId);
}