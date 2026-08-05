package com.huzaifa.url_shortener_service.controller;

import com.huzaifa.url_shortener_service.dto.UrlRequest;
import com.huzaifa.url_shortener_service.dto.UrlShortenerResponse;
import com.huzaifa.url_shortener_service.service.UrlShortenerService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api")
public class UrlShortenerController {

    final UrlShortenerService urlShortenerService;

    public UrlShortenerController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @PostMapping("/createurl")
    public UrlShortenerResponse longUrl(@RequestBody UrlRequest request) {
        return urlShortenerService.shortCode(request);
    }

    // redirect to the original long url
    @GetMapping("/{shortCode}")
    @Cacheable(value = "longUrl", key = "#shortcode")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String longUrl = urlShortenerService.redirect(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(longUrl)).build();
    }
}
