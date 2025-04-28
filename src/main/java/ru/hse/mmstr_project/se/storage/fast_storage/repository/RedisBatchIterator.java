package ru.hse.mmstr_project.se.storage.fast_storage.repository;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public class RedisBatchIterator<T> implements Iterator<List<T>>, Closeable {
    private final long startTime;
    private final long endTime;
    private final int batchSize;
    private final BatchFetcher batchFetcher;
    private final EntityConverter<T> entityConverter;

    private long currentOffset = 0;
    private List<T> nextBatch = null;
    private boolean noKeys = false;

    @FunctionalInterface
    public interface BatchFetcher {
        Set<String> fetch(long startTime, long endTime, long offset, int batchSize);
    }

    @FunctionalInterface
    public interface EntityConverter<T> {
        List<T> apply(List<String> entities);
    }

    public RedisBatchIterator(
            long startTime,
            long endTime,
            int batchSize,
            BatchFetcher batchFetcher,
            EntityConverter<T> entityConverter) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.batchSize = batchSize;
        this.batchFetcher = batchFetcher;
        this.entityConverter = entityConverter;
    }

    @Override
    public boolean hasNext() {
        if (nextBatch != null) {
            return true;
        }

        loadNextBatch();
        return !noKeys;
    }

    @Override
    public List<T> next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more batches in iterator");
        }

        List<T> result = nextBatch;
        nextBatch = null;
        return result;
    }

    private void loadNextBatch() {
        Set<String> keys = batchFetcher.fetch(startTime, endTime, currentOffset, batchSize);

        if (keys.isEmpty()) {
            noKeys = true;
            nextBatch = Collections.emptyList();
            return;
        }

        nextBatch = entityConverter.apply(new ArrayList<>(keys));
        currentOffset += keys.size();
    }

    @Override
    public void close() {
        nextBatch = null;
    }
}