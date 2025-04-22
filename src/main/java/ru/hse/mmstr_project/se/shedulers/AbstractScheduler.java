package ru.hse.mmstr_project.se.shedulers;

import org.springframework.transaction.annotation.Transactional;
import ru.hse.mmstr_project.se.storage.common.entity.system.SchedulersState;
import ru.hse.mmstr_project.se.storage.common.repository.system.SchedulersStateRepository;

import java.time.Instant;

public abstract class AbstractScheduler {

    private final SchedulersStateRepository schedulersStateRepository;

    protected AbstractScheduler(SchedulersStateRepository schedulersStateRepository) {
        this.schedulersStateRepository = schedulersStateRepository;
    }

    @Transactional
    protected void saveLastProcessedTime(Instant time) {
        schedulersStateRepository.save(new SchedulersState(getSchedulerId(), time));
    }

    @Transactional(readOnly = true)
    protected Instant getLastProcessedTime() {
        return schedulersStateRepository.findById(getSchedulerId())
                .map(SchedulersState::getFetchTime)
                .orElse(Instant.now());
    }

    protected abstract Long getSchedulerId();
}
