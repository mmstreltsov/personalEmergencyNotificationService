package ru.hse.mmstr_project.se.shedulers.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class FastSchedulersMetrics {

    private final Counter counter;
    private final Counter batchCounter;
    private final Counter duplicatesFilteredCounter;
    private final Timer requestTimer;

    public FastSchedulersMetrics(MeterRegistry meterRegistry) {
        this.counter = Counter.builder("scheduler.fast.processing.count")
                .description("Total amount of processed items")
                .tag("place", "schedulers")
                .register(meterRegistry);
        this.batchCounter = Counter.builder("scheduler.fast.batches.count")
                .description("Total amount of batches processed")
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

    public void incBatches() {
        batchCounter.increment();
    }

    public void incDuplicatesFiltered(int v) {
        duplicatesFilteredCounter.increment(v);
    }

    public void measureRequest(Runnable action) {
        requestTimer.record(action);
    }
}
