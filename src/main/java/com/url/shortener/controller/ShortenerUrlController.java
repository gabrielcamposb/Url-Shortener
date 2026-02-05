package com.url.shortener.controller;

import com.url.shortener.exception.BadRequestException;
import com.url.shortener.model.ShortenedUrl;
import com.url.shortener.service.UrlShortenerService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/urls")
public class ShortenerUrlController {

    private final UrlShortenerService service;

    @Value("${app.base-url}")
    private String baseUrl;

    public ShortenerUrlController(UrlShortenerService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> shorten(
            @RequestParam @NotBlank @org.hibernate.validator.constraints.URL String url,
            @RequestParam(defaultValue = "24") String hours) {

        long hoursLong;
        try {
            hoursLong = Long.parseLong(hours);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Hours must be a number greater than zero");
        }

        if (hoursLong <= 0) {
            throw new BadRequestException("Hours must be greater than zero");
        }

        ShortenedUrl shortenedUrl = service.create(url, Duration.ofHours(hoursLong));

        return ResponseEntity.ok(Map.of(
                "shortenedUrl", baseUrl + "/" + shortenedUrl.getShortCode()
        ));
    }
}
