package ru.hse.mmstr_project.se.shedulers;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import ru.hse.mmstr_project.se.storage.common.entity.system.SchedulersState;
import ru.hse.mmstr_project.se.storage.common.repository.system.SchedulersStateRepository;

import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

public abstract class AbstractScheduler {

    private final SchedulersStateRepository schedulersStateRepository;

    protected AbstractScheduler(SchedulersStateRepository schedulersStateRepository) {
        this.schedulersStateRepository = schedulersStateRepository;
    }

    protected void saveLastProcessedTime(Instant time) {
        schedulersStateRepository.save(new SchedulersState(getSchedulerId(), time));
    }

    protected void markLastProcessedLikeUnsuccessfully() {
        schedulersStateRepository.setLastTryFailedById(getSchedulerId());
    }

    protected SchedulersStateDto getLastProcessedTime() {
        return schedulersStateRepository.findById(getSchedulerId())
                .map(it -> new SchedulersStateDto(it.getFetchTime(), it.isSuccessLastTry()))
                .orElse(new SchedulersStateDto(Instant.now(), true));
    }

    protected abstract Long getSchedulerId();

    protected void clearExecutorQueue(Executor executor) {
        ThreadPoolTaskExecutor exec = (ThreadPoolTaskExecutor) executor;
        BlockingQueue<Runnable> queue = exec.getThreadPoolExecutor().getQueue();
        queue.clear();
    }

    protected record SchedulersStateDto(Instant fetchTime, boolean lastTrySuccess) {}
}
