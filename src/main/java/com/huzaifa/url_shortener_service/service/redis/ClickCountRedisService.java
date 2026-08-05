package com.huzaifa.url_shortener_service.service.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ClickCountRedisService {
    private final RedisTemplate<String,String> redisTemplate;

    public ClickCountRedisService(RedisTemplate<String,String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void increment(String shortCode) {
        redisTemplate.opsForValue().increment("clicks: "+shortCode);
    }

    public String get(String shortCode) {
        return redisTemplate.opsForValue().get("clicks: "+shortCode);
    }
}
