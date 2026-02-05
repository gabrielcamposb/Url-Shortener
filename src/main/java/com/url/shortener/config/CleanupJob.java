package com.url.shortener.config;

import com.url.shortener.repository.ShortenedUrlRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CleanupJob {

    private final ShortenedUrlRepository repository;

    public CleanupJob(ShortenedUrlRepository repository) {
        this.repository = repository;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void cleanupExpiredUrls() {
        repository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}
