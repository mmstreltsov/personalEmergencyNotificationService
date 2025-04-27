package ru.hse.mmstr_project.se.storage.common.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class BatchPaginationIterator<E, T> implements Iterator<List<T>> {
    private final Function<Pageable, Page<E>> pageSupplier;
    private final Function<E, T> dtoMapper;
    private final int batchSize;
    private int currentPage = 0;
    private final AtomicBoolean cancelled;

    private boolean hasMorePages = true;

    public BatchPaginationIterator(
            Function<Pageable, Page<E>> pageSupplier,
            Function<E, T> dtoMapper,
            int batchSize,
            AtomicBoolean cancelled) {
        this.pageSupplier = pageSupplier;
        this.dtoMapper = dtoMapper;
        this.batchSize = batchSize;
        this.cancelled = cancelled;
    }

    @Override
    public boolean hasNext() {
        if (cancelled != null && cancelled.get()) {
            return false;
        }

        return hasMorePages;
    }

    @Override
    public List<T> next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        Page<E> page = pageSupplier.apply(PageRequest.of(currentPage++, batchSize));
        hasMorePages = page.getNumber() < page.getTotalPages();

        return page.getContent().stream()
                .map(dtoMapper)
                .toList();
    }
}