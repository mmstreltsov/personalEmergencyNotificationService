package ru.hse.mmstr_project.se.shedulers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.hse.mmstr_project.se.service.FastSchedulerManager;
import ru.hse.mmstr_project.se.storage.common.repository.system.SchedulersStateRepository;
import ru.hse.mmstr_project.se.storage.fast_storage.dto.IncidentMetadataDto;
import ru.hse.mmstr_project.se.storage.fast_storage.repository.RedisItemRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

@Component
public class FastScheduler extends AbstractScheduler {

    private static final long SCHEDULER_ID = 1;
    private static final int BATCH_SIZE = 128;
    private static final long FROM = Instant.EPOCH.getEpochSecond();

    private final Executor taskExecutor;
    private final RedisItemRepository repository;
    private final FastSchedulerManager fastSchedulerManager;

    public FastScheduler(
            @Qualifier("taskExecutorForFastStorage") Executor taskExecutor,
            RedisItemRepository repository,
            SchedulersStateRepository schedulersStateRepository, FastSchedulerManager fastSchedulerManager) {
        super(schedulersStateRepository);
        this.taskExecutor = taskExecutor;
        this.repository = repository;
        this.fastSchedulerManager = fastSchedulerManager;
    }


    @Scheduled(fixedDelayString = "${app.scheduler.fast-database-scan.fixed-delay}")
    @Transactional
    public void ohohohoh() {
        Instant to = Instant.now().plus(5, ChronoUnit.SECONDS);

        Iterator<List<IncidentMetadataDto>> iterator = repository.getIteratorByFirstTimeToActivateLessThan(
                FROM,
                to.getEpochSecond(),
                BATCH_SIZE);

        while (iterator.hasNext()) {
            List<IncidentMetadataDto> incidentMetadataDto = iterator.next();
            taskExecutor.execute(() -> fastSchedulerManager.handle(incidentMetadataDto));
        }
        saveLastProcessedTime(to);
    }

    @Override
    protected Long getSchedulerId() {
        return SCHEDULER_ID;
    }
}
