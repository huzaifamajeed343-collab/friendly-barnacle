package com.huzaifa.url_shortener_service.service.redis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.huzaifa.url_shortener_service.model.UrlMappingModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, UrlMappingModel> urlTemplate(RedisConnectionFactory redisConnection) {
        RedisTemplate<String,UrlMappingModel> template = new RedisTemplate<>();

        template.setConnectionFactory(redisConnection);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        // set the key type as string
        template.setKeySerializer(new StringRedisSerializer());

        // set the value type as UrlMappingModel
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(mapper,UrlMappingModel.class));

        template.afterPropertiesSet();

        return template;
    }
}
