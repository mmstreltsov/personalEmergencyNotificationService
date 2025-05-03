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
    private final List<String> indexes;

    private long currentOffset = 0;
    private int currentIndexId = 0;
    private List<T> nextBatch = null;
    private boolean noKeys = false;

    @FunctionalInterface
    public interface BatchFetcher {
        Set<String> fetch(long startTime, String indexId, long endTime, long offset, int batchSize);
    }

    @FunctionalInterface
    public interface EntityConverter<T> {
        List<T> apply(List<String> entities);
    }

    @FunctionalInterface
    public interface IndexFetcher {
        List<String> apply();
    }

    public RedisBatchIterator(
            long startTime,
            long endTime,
            int batchSize,
            BatchFetcher batchFetcher,
            EntityConverter<T> entityConverter,
            IndexFetcher indexFetcher) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.batchSize = batchSize;
        this.batchFetcher = batchFetcher;
        this.entityConverter = entityConverter;
        indexes = indexFetcher.apply();
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
        if (indexes.size() <= currentIndexId) {
            noKeys = true;
            nextBatch = Collections.emptyList();
            return;
        }

        String index = indexes.get(currentIndexId);
        Set<String> keys = batchFetcher.fetch(startTime, index, endTime, currentOffset, batchSize);

        if (keys.isEmpty()) {
            currentIndexId++;
            currentOffset = 0;
            loadNextBatch();
        }

        nextBatch = entityConverter.apply(new ArrayList<>(keys));
        currentOffset += keys.size();
    }

    @Override
    public void close() {
        nextBatch = null;
    }
}