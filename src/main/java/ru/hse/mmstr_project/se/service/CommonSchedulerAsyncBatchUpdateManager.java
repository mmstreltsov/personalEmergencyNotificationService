package ru.hse.mmstr_project.se.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;
import ru.hse.mmstr_project.se.service.storage.ScenarioStorage;
import ru.hse.mmstr_project.se.storage.common.dto.ScenarioDto;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

@Service
public class CommonSchedulerAsyncBatchUpdateManager {
    private final ScenarioStorage scenarioStorage;
    private final MeterRegistry meterRegistry;

    private final int maxQueueSize = 1_000_000;
    private final int maxBatchSize = 1024;
    private final Duration flushInterval = Duration.ofSeconds(7);
    private final Duration guaranteedFlushInterval = Duration.ofMinutes(2);

    private final ConcurrentLinkedQueue<UpdateTask> updateQueue = new ConcurrentLinkedQueue<>();
    private final AtomicLong queueSize = new AtomicLong(0L);
    private final Timer flushTimer;
    private final ReentrantLock flushLock = new ReentrantLock();

    private volatile Instant lastFlushTime = Instant.now();

    private static class UpdateTask {
        final ScenarioDto scenario;
        final Function<ScenarioDto, ScenarioDto> updateFunction;
        final Instant createdAt;

        UpdateTask(ScenarioDto scenario, Function<ScenarioDto, ScenarioDto> updateFunction) {
            this.scenario = scenario;
            this.updateFunction = updateFunction;
            this.createdAt = Instant.now();
        }
    }

    public CommonSchedulerAsyncBatchUpdateManager(
            ScenarioStorage scenarioStorage,
            MeterRegistry meterRegistry) {
        this.scenarioStorage = scenarioStorage;
        this.meterRegistry = meterRegistry;
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(
                2,
                new ThreadFactoryBuilder()
                        .setNameFormat("scenario-update-%d")
                        .setDaemon(true)
                        .build());

        scheduler.scheduleWithFixedDelay(
                this::tryFlush,
                flushInterval.toMillis(),
                flushInterval.toMillis(),
                TimeUnit.MILLISECONDS);

        scheduler.scheduleWithFixedDelay(
                this::guaranteedFlush,
                guaranteedFlushInterval.toMillis(),
                guaranteedFlushInterval.toMillis(),
                TimeUnit.MILLISECONDS);

        Gauge.builder("scheduler.common.async.update.queue.size", queueSize, (q) -> 1.0 * q.get() / maxQueueSize)
                .description("Queue size ratio for async updater")
                .tag("place", "schedulers")
                .register(meterRegistry);
        this.flushTimer = Timer
                .builder("scheduler.common.async.update.flush.duration")
                .description("Time taken to flush scenario updates to storage")
                .publishPercentileHistogram()
                .register(meterRegistry);
    }

    public boolean queueUpdates(List<ScenarioDto> scenarios, Function<ScenarioDto, ScenarioDto> updateFunction) {
        if (scenarios.isEmpty()) {
            return true;
        }

        if (queueSize.get() + scenarios.size() > maxQueueSize) {
            return false;
        }

        for (ScenarioDto scenario : scenarios) {
            updateQueue.add(new UpdateTask(scenario, updateFunction));
        }
        queueSize.addAndGet(scenarios.size());

        return true;
    }


    public void tryFlush() {
        if (queueSize.get() == 0) return;

        if (!flushLock.tryLock()) return;

        try {
            flushInternal();
        } catch (Exception ignored) {
        } finally {
            flushLock.unlock();
        }
    }

    private void guaranteedFlush() {
        if (queueSize.get() == 0) return;

        if (!flushLock.tryLock()) return;

        try {
            Timer.Sample sample = Timer.start(meterRegistry);

            Instant threshold = Instant.now().minus(guaranteedFlushInterval);
            List<UpdateTask> oldTasks = new ArrayList<>();

            UpdateTask task;
            while ((task = updateQueue.peek()) != null && task.createdAt.isBefore(threshold)) {
                task = updateQueue.poll();
                if (task != null) {
                    oldTasks.add(task);
                }
            }

            if (!oldTasks.isEmpty()) {
                flushTasks(oldTasks);
            }

            sample.stop(flushTimer);
        } catch (Exception ignored) {
        } finally {
            flushLock.unlock();
        }
    }

    private void flushInternal() {
        lastFlushTime = Instant.now();

        List<UpdateTask> tasksToProcess = new ArrayList<>(maxBatchSize);
        int tasksToTake = (int) Math.min(queueSize.get(), maxBatchSize);

        for (int i = 0; i < tasksToTake; i++) {
            UpdateTask task = updateQueue.poll();
            if (task == null) break;

            tasksToProcess.add(task);
        }

        if (tasksToProcess.isEmpty()) return;

        queueSize.addAndGet(-tasksToProcess.size());

        try {
            Timer.Sample sample = Timer.start(meterRegistry);
            flushTasks(tasksToProcess);
            sample.stop(flushTimer);
        } catch (Exception ignored) {
        }
    }

    private void flushTasks(List<UpdateTask> tasks) {
        if (tasks.isEmpty()) return;

        List<ScenarioDto> updatedScenarios = tasks.stream()
                .map(task -> task.updateFunction.apply(task.scenario))
                .toList();
        scenarioStorage.saveAll(updatedScenarios);
    }
}
