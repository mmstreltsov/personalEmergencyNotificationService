package ru.hse.mmstr_project.se.shedulers.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class FastSchedulersMetrics {

    private final Counter counter;
    private final AtomicLong batchCounter = new AtomicLong(0L);
    private final Counter duplicatesFilteredCounter;
    private final Timer requestTimer;

    public FastSchedulersMetrics(MeterRegistry meterRegistry) {
        this.counter = Counter.builder("scheduler.fast.processing.count")
                .description("Total amount of processed items")
                .tag("place", "schedulers")
                .register(meterRegistry);
        Gauge.builder("scheduler.fast.batches.count", batchCounter, AtomicLong::get)
                .description("Time window for db request")
                .tag("place", "schedulers")
                .register(meterRegistry);
        this.duplicatesFilteredCounter = Counter.builder("scheduler.fast.duplicates.filtered.count")
                .description("Total amount of duplicates")
                .tag("place", "schedulers")
                .register(meterRegistry);
        this.requestTimer = Timer.builder("scheduler.fast.batch.processing.time")
                .description("Batch processing time")
                .tag("place", "schedulers")
                .publishPercentileHistogram()
                .register(meterRegistry);
    }

    public void incProcessedItems(int v) {
        counter.increment(v);
    }

    public void flushBatches() {
        batchCounter.set(0L);
    }

    public void incBatches() {
        batchCounter.incrementAndGet();
    }

    public void incDuplicatesFiltered(int v) {
        duplicatesFilteredCounter.increment(v);
    }

    public void measureRequest(Runnable action) {
        requestTimer.record(action);
    }
}
