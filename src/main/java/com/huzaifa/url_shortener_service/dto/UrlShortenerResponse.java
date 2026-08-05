package com.huzaifa.url_shortener_service.dto;

import jakarta.validation.constraints.NotBlank;

public record UrlShortenerResponse(@NotBlank String shortUrl) {
}
