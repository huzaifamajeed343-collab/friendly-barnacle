package com.huzaifa.url_shortener_service.service.redis;

import com.huzaifa.url_shortener_service.exception.UrlNotFoundException;
import com.huzaifa.url_shortener_service.micrometer.LatencyTimer;
import com.huzaifa.url_shortener_service.model.UrlMappingModel;
import com.huzaifa.url_shortener_service.repository.UrlMappingRepo;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
@Service
public class UrlCachingRedisService {
    private final RedisTemplate<String,UrlMappingModel> redisTemplate;
    private final UrlMappingRepo urlMappingRepo;
    private final Counter cacheHit;
    private final Counter cacheMiss;
    private final LatencyTimer dbLatency;
    private final LatencyTimer redisLatency;

    public UrlCachingRedisService
            (RedisTemplate<String,UrlMappingModel> redisTemplate,
             UrlMappingRepo urlMappingRepo,
             MeterRegistry registry) {

        this.redisTemplate = redisTemplate;
        this.urlMappingRepo = urlMappingRepo;

        cacheHit = Counter.builder("url_cache_hit_total")
                .description("number of cache hit")
                .register(registry);

        cacheMiss = Counter.builder("url_cache_miss_total")
                .description("number of cache miss")
                .register(registry);

        redisLatency = new LatencyTimer(
                registry,
                "redis_lookup_time",
                "redis latency"
        );

        dbLatency = new LatencyTimer(
                registry,
                "db_lookup_time",
                "database latency"
        );
    }

    public void saveUrl(UrlMappingModel url, String shortCode) {
        redisTemplate.opsForValue().set("url: "+shortCode,url);
    }

    public String getLongUrl(String shortCode) {

        UrlMappingModel url = redisLatency.record(() -> redisTemplate.opsForValue().get("url: "+shortCode));
        System.out.println(url);

        // cache miss
        if (url == null) {
            cacheMiss.increment();
            url = dbLatency.record(() -> urlMappingRepo.findByShortCode(shortCode))
                    .orElseThrow(() -> new UrlNotFoundException("url not found"));

            saveUrl(url, url.getShortCode());

        } else {
            // cache hit
            cacheHit.increment();
        }
        return url.getLongUrl();
    }
}
