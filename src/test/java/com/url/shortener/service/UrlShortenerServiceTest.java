package com.url.shortener.service;

import com.url.shortener.model.ShortenedUrl;
import com.url.shortener.repository.ShortenedUrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceTest {

    @Mock
    private ShortenedUrlRepository repository;

    @Mock
    private RedisTemplate<String, Long> redisTemplate;

    @Mock
    private ValueOperations<String, Long> valueOperations;

    @InjectMocks
    private UrlShortenerService service;

    @BeforeEach
    void setup() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void shouldCreateShortUrl() {
        when(repository.findByShortCode(any())).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ShortenedUrl result = service.create("https://google.com", Duration.ofHours(24));

        assertThat(result).isNotNull();
        assertThat(result.getShortCode()).isNotBlank();

        verify(repository).save(any());
        verify(valueOperations)
                .set(startsWith("Clicks:"), eq(0L), any(Duration.class));
    }

    @Test
    void shouldReturnOriginalUrlIfValid() {
        ShortenedUrl valid = new ShortenedUrl();
        valid.setShortCode("ok123");
        valid.setOriginalUrl("https://google.com");
        valid.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        when(repository.findByShortCode("ok123"))
                .thenReturn(Optional.of(valid));

        String original = service.findOriginalUrl("ok123");

        assertThat(original).isEqualTo("https://google.com");
    }

    @Test
    void shouldThrowIfExpired() {
        ShortenedUrl expired = new ShortenedUrl();
        expired.setShortCode("Expired");
        expired.setExpiresAt(LocalDateTime.now().minusMinutes(1));

        when(repository.findByShortCode("Expired"))
                .thenReturn(Optional.of(expired));

        assertThatThrownBy(() -> service.findOriginalUrl("Expired"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Expired");
    }

    @Test
    void shouldIncrementClickCounter() {
        service.registerClick("abc123");

        verify(valueOperations).increment("Clicks: abc123");
    }
}
