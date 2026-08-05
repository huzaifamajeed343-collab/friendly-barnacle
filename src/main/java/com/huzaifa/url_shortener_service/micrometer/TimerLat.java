package com.huzaifa.url_shortener_service.micrometer;

import java.util.function.Supplier;

public interface TimerLat {
    <T> T record(Supplier<T> operation);
}
