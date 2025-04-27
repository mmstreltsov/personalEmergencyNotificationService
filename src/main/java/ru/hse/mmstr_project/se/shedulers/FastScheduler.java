package ru.hse.mmstr_project.se.shedulers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.service.FastSchedulerManager;
import ru.hse.mmstr_project.se.shedulers.metrics.FastSchedulersMetrics;
import ru.hse.mmstr_project.se.storage.common.repository.system.SchedulersStateRepository;
import ru.hse.mmstr_project.se.storage.fast_storage.dto.IncidentMetadataDto;
import ru.hse.mmstr_project.se.storage.fast_storage.repository.RedisItemRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

@Component
public class FastScheduler extends AbstractScheduler {

    private static final long SCHEDULER_ID = 2;
    private static final int SECONDS_TO_EXTRA_SCAN = 5;
    private static final int BATCH_SIZE = 128;
    private static final long FROM = Instant.EPOCH.getEpochSecond() * 1000;

    private final Executor taskExecutor;
    private final RedisItemRepository repository;
    private final FastSchedulerManager fastSchedulerManager;
    private final FastSchedulersMetrics fastSchedulersMetrics;

    public FastScheduler(
            @Qualifier("taskExecutorForFastStorage") Executor taskExecutor,
            RedisItemRepository repository,
            SchedulersStateRepository schedulersStateRepository,
            FastSchedulerManager fastSchedulerManager,
            FastSchedulersMetrics fastSchedulersMetrics) {
        super(schedulersStateRepository);
        this.taskExecutor = taskExecutor;
        this.repository = repository;
        this.fastSchedulerManager = fastSchedulerManager;
        this.fastSchedulersMetrics = fastSchedulersMetrics;
    }

    @Scheduled(fixedDelayString = "${app.scheduler.fast-database-scan.fixed-delay}")
    public void ohohohoh() {
        Instant to = Instant.now().plus(SECONDS_TO_EXTRA_SCAN, ChronoUnit.SECONDS);

        Iterator<List<IncidentMetadataDto>> iterator = repository.getIteratorByFirstTimeToActivateLessThan(
                FROM,
                to.getEpochSecond() * 1000,
                BATCH_SIZE);

        clearExecutorQueue(taskExecutor);
        fastSchedulersMetrics.flushBatches();
        while (iterator.hasNext()) {
            List<IncidentMetadataDto> incidentMetadataDto = iterator.next();
            if (incidentMetadataDto.isEmpty()) {
                continue;
            }

            try {
                taskExecutor.execute(() -> fastSchedulerManager.handle(incidentMetadataDto));
            } catch (RejectedExecutionException ignored) {
                markLastProcessedLikeUnsuccessfully();
                return;
            }
            fastSchedulersMetrics.incProcessedItems(incidentMetadataDto.size());
            fastSchedulersMetrics.incBatches();
        }
        saveLastProcessedTime(to);
    }

    @Override
    protected Long getSchedulerId() {
        return SCHEDULER_ID;
    }
}
