package com.huzaifa.url_shortener_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;


public record UrlRequest(
        @NotBlank
        @Pattern(
                regexp = "https?://.*",
                message = "Must be a valid URL starting with http or https"
        )
        String longUrl
) {}
