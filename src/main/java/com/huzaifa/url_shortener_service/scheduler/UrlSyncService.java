package com.huzaifa.url_shortener_service.scheduler;

import com.huzaifa.url_shortener_service.model.UrlMappingModel;
import com.huzaifa.url_shortener_service.repository.UrlMappingRepo;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


public class UrlSyncService {

    private final RedisTemplate<String,String> redisTemplate;
    private final UrlMappingRepo urlMappingRepo;

    public UrlSyncService(
            RedisTemplate<String,String> redisTemplate,
            UrlMappingRepo urlMappingRepo
    ) {

        this.redisTemplate = redisTemplate;
        this.urlMappingRepo = urlMappingRepo;
    }

    @Scheduled(fixedRate = 77777)
    public void syncClickCount() {

        try(Cursor<String> cursor = redisTemplate.scan(ScanOptions
                .scanOptions()
                .match("clicks: *")
                .count(2000)
                .build()
        )) {

            // iterate through the keys and rename them for concurrency reason
            while (cursor.hasNext()) {
                String key = cursor.next();
                String shortCode = key.replace("clicks: ", "");

                redisTemplate.rename(key, "sync: " + shortCode);
                String clickCounts = redisTemplate.opsForValue().get("sync: " + shortCode);

                UrlMappingModel url = urlMappingRepo.findByShortCode(shortCode).orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "url not found"
                        )
                );

                //assert clickCounts != null;
                if (clickCounts == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "url not found");
                url.setClickCount(
                        url.getClickCount() + Long.parseLong(clickCounts)
                );

                urlMappingRepo.save(url);

                redisTemplate.delete("sync " + shortCode);
            }
        }
    }
}
