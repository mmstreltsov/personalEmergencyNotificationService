package ru.hse.mmstr_project.se.spam_detector;

import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.spam_detector.methods.SpamDetector;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class SpamDetectorManager {
    private static final long WAITING_TIME_IN_MS = 5_000;

    private final List<SpamDetector> spamDetector;
    private final ExecutorService executor;

    public SpamDetectorManager(
            List<SpamDetector> spamDetector,
            ExecutorService executorServiceForSpamDetector) {
        this.spamDetector = spamDetector;
        this.executor = executorServiceForSpamDetector;
    }

    public boolean isSpam(String obj) {
        List<CompletableFuture<Boolean>> list = spamDetector.stream()
                .map(it -> CompletableFuture.supplyAsync(() -> it.isSpam(obj), executor))
                .toList();

        CompletableFuture<Boolean> future = CompletableFuture
                .anyOf(list.toArray(new CompletableFuture[0]))
                .thenApply(res -> (Boolean) res);

        try {
            return future.get(WAITING_TIME_IN_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return false;
        }
    }
}
