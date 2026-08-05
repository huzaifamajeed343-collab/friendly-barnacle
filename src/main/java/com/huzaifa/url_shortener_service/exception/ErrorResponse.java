package com.huzaifa.url_shortener_service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Setter
@Getter
public class ErrorResponse {
    private final int status;
    private final String message;
    private final LocalDateTime timeStamp;
}
