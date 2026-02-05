package com.url.shortener.service;

import com.url.shortener.exception.BadRequestException;
import com.url.shortener.model.ShortenedUrl;
import com.url.shortener.repository.ShortenedUrlRepository;
import com.url.shortener.util.ShortenedUrlGenerator;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class UrlShortenerService {

    private final ShortenedUrlRepository repository;
    private final RedisTemplate<String, Long> redisTemplate;

    public UrlShortenerService(ShortenedUrlRepository repository, RedisTemplate<String, Long> redisTemplate) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
    }

    public ShortenedUrl create(String originalUrl, Duration ttl) {
        if (ttl.isZero() || ttl.isNegative()) {
            throw new BadRequestException("TTL must be greater than zero");
        }

        ShortenedUrl url = new ShortenedUrl();
        url.setOriginalUrl(originalUrl);
        url.setShortCode(generateUniqueCode());
        url.setExpiresAt(LocalDateTime.now().plus(ttl));

        ShortenedUrl saved = repository.save(url);

        redisTemplate.opsForValue()
                .set("Clicks:" + saved.getShortCode(), 0L, ttl);

        return saved;
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = ShortenedUrlGenerator.generate();
        } while (repository.findByShortCode(code).isPresent());
        return code;
    }

    @Cacheable(value = "shortUrls", key = "#code", unless = "#result == null")
    public String findOriginalUrl(String code) {
        ShortenedUrl url = repository.findByShortCode(code)
                .orElseThrow(() -> new RuntimeException("URL not found"));

        if (url.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("URL expired");
        }

        return url.getOriginalUrl();
    }

    public void registerClick(String code) {
        redisTemplate.opsForValue().increment("Clicks:" + code);
    }

    @Scheduled(fixedDelay = 60000)
    public void syncClicksToDatabase() {
        repository.findAll().forEach(url -> {
            Long clicks = redisTemplate.opsForValue()
                    .get("Clicks:" + url.getShortCode());

            if (clicks != null) {
                url.setClickCount(clicks);
                repository.save(url);
            }
        });
    }
}
