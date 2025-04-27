package ru.hse.mmstr_project.se.storage.common.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class PaginationIterator<T> implements Iterator<T> {
    private final Function<Pageable, Page<?>> pageSupplier;
    private final Function<Object, T> mapper;
    private final int pageSize;
    private final AtomicBoolean cancelled;

    private List<?> currentBatch = Collections.emptyList();
    private int currentIndex = 0;
    private int currentPage = 0;
    private boolean hasMore = true;

    public PaginationIterator(
            Function<Pageable, Page<?>> pageSupplier,
            Function<Object, T> mapper,
            int pageSize,
            AtomicBoolean cancelled) {
        this.pageSupplier = pageSupplier;
        this.mapper = mapper;
        this.pageSize = pageSize;
        this.cancelled = cancelled;
        fetchNextBatchIfNeeded();
    }

    @Override
    public boolean hasNext() {
        if (cancelled != null && cancelled.get()) {
            return false;
        }

        fetchNextBatchIfNeeded();
        return currentIndex < currentBatch.size();
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        Object entity = currentBatch.get(currentIndex++);
        return mapper.apply(entity);
    }

    private void fetchNextBatchIfNeeded() {
        if (currentIndex >= currentBatch.size() && hasMore) {
            Page<?> page = pageSupplier.apply(PageRequest.of(currentPage++, pageSize));
            currentBatch = page.getContent();
            currentIndex = 0;
            hasMore = !currentBatch.isEmpty() && page.hasNext();
        }
    }
}