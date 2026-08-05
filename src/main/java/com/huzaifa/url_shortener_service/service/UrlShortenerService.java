package com.huzaifa.url_shortener_service.service;

import com.huzaifa.url_shortener_service.dto.UrlRequest;
import com.huzaifa.url_shortener_service.dto.UrlShortenerResponse;
import com.huzaifa.url_shortener_service.exception.UrlNotFoundException;
import com.huzaifa.url_shortener_service.model.UrlMappingModel;
import com.huzaifa.url_shortener_service.repository.UrlMappingRepo;
import com.huzaifa.url_shortener_service.service.redis.ClickCountRedisService;
import com.huzaifa.url_shortener_service.service.redis.UrlCachingRedisService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Random;

@Service
public class UrlShortenerService {
    private final UrlMappingRepo urlMappingRepo;
    private final ClickCountRedisService clickCountRedisService;
    private final UrlCachingRedisService urlCachingRedisService;
    final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public UrlShortenerService(
            UrlMappingRepo urlMappingRepo,
            ClickCountRedisService clickCountRedisService,
            UrlCachingRedisService urlCachingRedisService) {

        this.urlMappingRepo = urlMappingRepo;
        this.clickCountRedisService = clickCountRedisService;
        this.urlCachingRedisService = urlCachingRedisService;
    }

    // generate the short code
    public UrlShortenerResponse shortCode(UrlRequest request) {
        String shortCode =  generateUniqueCode();

        UrlMappingModel newUrl = UrlMappingModel.builder()
                .shortCode(shortCode)
                .longUrl(request.longUrl())
                .build();

        urlMappingRepo.save(newUrl);

        // save both url and its associated shortcode in redis
        urlCachingRedisService.saveUrl(newUrl, shortCode);

        return new UrlShortenerResponse("http://localhost:8080/api" + shortCode);
    }

    public String redirect(String shortCode) {

        String longUrl = urlCachingRedisService.getLongUrl(shortCode);

        clickCountRedisService.increment(shortCode);
        return longUrl;
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = generateRandomCode();
        } while (urlMappingRepo.existsByShortCode(code));
        return code;
    }

    private String generateRandomCode() {
        final int codeLength = 6;
        Random random = new Random();

        StringBuilder codeString = new StringBuilder(codeLength);
        for (int i = 0; i < codeLength; i++) {
            codeString.append(BASE62.charAt(random.nextInt(62)));
        }
        return codeString.toString();
    }
}
