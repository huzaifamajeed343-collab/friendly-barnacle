package com.huzaifa.url_shortener_service.micrometer;

import io.micrometer.core.instrument.Timer;

import io.micrometer.core.instrument.MeterRegistry;

import java.util.function.Supplier;

public class LatencyTimer implements TimerLat{


    private final Timer timer;

    public LatencyTimer(MeterRegistry registry, String name, String description) {
        timer = Timer.builder(name)
                .description(description)
                .register(registry);
    }


    @Override
    public <T> T record(Supplier<T> operation) {
        return timer.record(operation);
    }
}
