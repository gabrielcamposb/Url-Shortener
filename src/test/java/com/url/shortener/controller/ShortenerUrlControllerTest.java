package com.url.shortener.controller;

import com.url.shortener.service.UrlShortenerService;
import com.url.shortener.model.ShortenedUrl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {ShortenerUrlController.class, RedirectController.class})
@TestPropertySource(properties = {
        "app.base-url=http://localhost:8080"
})
class ShortenerUrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlShortenerService service;

    @Test
    void shouldCreateShortUrl() throws Exception {
        ShortenedUrl url = new ShortenedUrl();
        url.setShortCode("abc123");

        when(service.create(anyString(), any(Duration.class)))
                .thenReturn(url);

        mockMvc.perform(post("/api/urls")
                        .param("url", "https://google.com")
                        .param("hours", "24"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortenedUrl")
                        .value("http://localhost:8080/abc123"));
    }

    @Test
    void shouldRedirectToOriginalUrl() throws Exception {
        when(service.findOriginalUrl("abc123"))
                .thenReturn("https://google.com");

        mockMvc.perform(get("/abc123"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://google.com"));

        verify(service).registerClick("abc123");
    }

    @Test
    void shouldReturn400WhenHoursIsZero() throws Exception {
        mockMvc.perform(post("/api/urls")
                        .param("url", "https://google.com")
                        .param("hours", "0"))
                .andExpect(status().isBadRequest());
    }
}
