package ru.hse.mmstr_project.se.shedulers;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class CommonSchedulersMetrics {

    private final Counter counter;
    private final AtomicLong timeWindowValueSec = new AtomicLong(0L);
    private final Timer requestTimer;

    public CommonSchedulersMetrics(MeterRegistry meterRegistry) {
        this.counter = Counter.builder("scheduler.common.processing.count")
                .description("Total amount of processed items")
                .tag("place", "schedulers")
                .register(meterRegistry);
        Gauge.builder("scheduler.common.window.value", timeWindowValueSec, AtomicLong::get)
                .description("Time window for db request")
                .tag("place", "schedulers")
                .register(meterRegistry);
        this.requestTimer = Timer.builder("scheduler.common.batch.processing.time")
                .description("Batch processing time")
                .tag("place", "schedulers")
                .publishPercentileHistogram() // Это включает гистограмму в Prometheus
                .register(meterRegistry);
    }

    public void inc(int v) {
        counter.increment(v);
    }

    public void setTimeWindowValueSec(long timeDelta) {
        timeWindowValueSec.set(timeDelta);
    }

    public void measureRequest(Runnable action) {
        requestTimer.record(action);
    }
}
