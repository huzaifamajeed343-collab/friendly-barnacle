package com.huzaifa.url_shortener_service.model;

import com.huzaifa.url_shortener_service.service.UrlShortenerService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Builder
@Getter
@Setter
@Table(name = "url_mappings")
@AllArgsConstructor
public class UrlMappingModel {
    public UrlMappingModel() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "short_code",nullable = false)
    private String shortCode;

    @Column(name = "long_url",nullable = false)
    private String longUrl;

    @Column(name = "created_at",nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "click_count",nullable = false)
    private long clickCount;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
