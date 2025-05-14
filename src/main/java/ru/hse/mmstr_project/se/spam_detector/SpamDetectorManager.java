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
        List<CompletableFuture<Boolean>> futures = spamDetector.stream()
                .map(it -> CompletableFuture.supplyAsync(() -> it.isSpam(obj), executor))
                .toList();

        try {
            CompletableFuture<List<Boolean>> allFutures = CompletableFuture.allOf(
                            futures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> futures.stream()
                            .map(CompletableFuture::join)
                            .toList());

            List<Boolean> results = allFutures.get(WAITING_TIME_IN_MS, TimeUnit.MILLISECONDS);
            return results.stream().anyMatch(Boolean::booleanValue);

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return futures.stream()
                    .filter(CompletableFuture::isDone)
                    .anyMatch(f -> {
                        try {
                            return f.getNow(false);
                        } catch (Exception ex) {
                            return false;
                        }
                    });
        }
    }
}
