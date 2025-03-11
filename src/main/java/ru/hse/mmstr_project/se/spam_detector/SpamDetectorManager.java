package ru.hse.mmstr_project.se.spam_detector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.spam_detector.methods.SpamDetector;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class SpamDetectorManager {
    private static final long WAITING_TIME_IN_MS = 1_000;
    private static final Logger log = LoggerFactory.getLogger(SpamDetectorManager.class);

    private final SpamDetector spamDetector;
    private final SpamDetector fallbackSpamDetector;
    private final ExecutorService executor;

    public SpamDetectorManager(
            @Qualifier("nlpSpamDetectorImpl") SpamDetector spamDetector,
            @Qualifier("regexpSpamDetectorImpl") SpamDetector fallbackSpamDetector,
            ExecutorService executor) {
        this.spamDetector = spamDetector;
        this.fallbackSpamDetector = fallbackSpamDetector;
        this.executor = executor;
    }

    public boolean isSpam(String obj) {
        try {
            return CompletableFuture
                    .supplyAsync(() -> spamDetector.isSpam(obj), executor)
                    .get(WAITING_TIME_IN_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.warn("Spam detector failed", e);
            return fallbackSpamDetector.isSpam(obj);
        }
    }
}
