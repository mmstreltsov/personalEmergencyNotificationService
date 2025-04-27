package ru.hse.mmstr_project.se.shedulers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.hse.mmstr_project.se.service.CommonSchedulerAsyncBatchUpdateManager;
import ru.hse.mmstr_project.se.service.CommonSchedulerManager;
import ru.hse.mmstr_project.se.service.storage.ScenarioStorage;
import ru.hse.mmstr_project.se.shedulers.metrics.CommonSchedulersMetrics;
import ru.hse.mmstr_project.se.storage.common.dto.ScenarioDto;
import ru.hse.mmstr_project.se.storage.common.repository.system.SchedulersStateRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

@Component
public class CommonScheduler extends AbstractScheduler {

    private static final long SCHEDULER_ID = 1;
    private static final int SECONDS_TO_EXTRA_SCAN = 10;
    private static final int BATCH_SIZE = 256;
    private static final Instant NEVER = Instant.ofEpochSecond(9224318015999L); // max timestamp in postgres

    private final Executor taskExecutor;
    private final ScenarioStorage scenarioStorage;
    private final CommonSchedulerManager manager;
    private final CommonSchedulersMetrics metrics;
    private final CommonSchedulerAsyncBatchUpdateManager asyncBatchUpdateManager;

    public CommonScheduler(
            @Qualifier("taskExecutorForCommonStorage") Executor taskExecutor,
            ScenarioStorage scenarioStorage,
            SchedulersStateRepository schedulersStateRepository,
            CommonSchedulerManager manager,
            CommonSchedulersMetrics metrics,
            CommonSchedulerAsyncBatchUpdateManager asyncBatchUpdateManager) {
        super(schedulersStateRepository);
        this.taskExecutor = taskExecutor;
        this.scenarioStorage = scenarioStorage;
        this.manager = manager;
        this.metrics = metrics;
        this.asyncBatchUpdateManager = asyncBatchUpdateManager;
    }

    @Scheduled(fixedDelayString = "${app.scheduler.common-database-scan.fixed-delay}")
    @Transactional
    public void ahahah() {
        SchedulersStateDto stateDto = getLastProcessedTime();

        Instant from = stateDto.fetchTime();
        Instant to = stateDto.lastTrySuccess()
                ? Instant.now().plus(SECONDS_TO_EXTRA_SCAN, ChronoUnit.SECONDS)
                : stateDto.fetchTime().plus(SECONDS_TO_EXTRA_SCAN, ChronoUnit.SECONDS);
        metrics.setTimeWindowValueSec(to.getEpochSecond() - from.getEpochSecond());

        if (from.isAfter(to)) {
            return;
        }

        if (!stateDto.lastTrySuccess()) {
            clearExecutorQueue(taskExecutor);
        }
        AtomicBoolean cancelled = new AtomicBoolean(false);
        metrics.flushBatches();

        Iterator<List<ScenarioDto>> batchIterator =
                scenarioStorage.iterateScenariosInBatches(from, to, BATCH_SIZE, cancelled);
        while (batchIterator.hasNext()) {
            List<ScenarioDto> scenarios = batchIterator.next();

            try {
                taskExecutor.execute(() -> manager.handle(scenarios));

                updateObjectsToNextPing(scenarios, to.plus(2, ChronoUnit.MINUTES));
                metrics.incProcessedItems(scenarios.size());
                metrics.incBatches();
            } catch (RejectedExecutionException e) {
                cancelled.set(true);
            }
        }

        if (cancelled.get()) {
            markLastProcessedLikeUnsuccessfully();
            return;
        }
        saveLastProcessedTime(to);
    }

    protected void updateObjectsToNextPing(List<ScenarioDto> scenarios, Instant minimalValue) {
        if (scenarios.isEmpty()) {
            return;
        }

        Function<ScenarioDto, ScenarioDto> updateFunction = scenarioDto -> {
            Instant nextTime = scenarioDto
                    .getListTimesToActivate()
                    .stream()
                    .filter(it -> it.isAfter(scenarioDto.getFirstTimeToActivate()))
                    .reduce(NEVER, (a, b) -> a.isBefore(b) ? a : b);

            return scenarioDto.toBuilder()
                    .firstTimeToActivate(nextTime.isAfter(minimalValue) ? nextTime : minimalValue)
                    .build();
        };

        boolean accepted = asyncBatchUpdateManager.queueUpdates(scenarios, updateFunction);

        if (!accepted) {
            List<ScenarioDto> updatedScenarios = scenarios.stream()
                    .map(updateFunction)
                    .toList();
            scenarioStorage.saveAll(updatedScenarios);
        }
    }

    @Override
    protected Long getSchedulerId() {
        return SCHEDULER_ID;
    }
}
