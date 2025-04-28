package ru.hse.mmstr_project.se.shedulers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
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

@Component
public class CommonScheduler extends AbstractScheduler {

    private static final long SCHEDULER_ID = 1;
    private static final int SECONDS_TO_EXTRA_SCAN = 10;
    private static final int WAIT_IF_NEEDED_MS = 10_000;
    private static final int BATCH_SIZE = 256;
    private static final int BATCH_SIZE_FOR_CHECKER = 1024;
    private static final Instant NEVER = Instant.ofEpochSecond(9224318015999L); // max timestamp in postgres

    private final Executor taskExecutor;
    private final ScenarioStorage scenarioStorage;
    private final CommonSchedulerManager manager;
    private final CommonSchedulersMetrics metrics;

    public CommonScheduler(
            @Qualifier("taskExecutorForCommonStorage") Executor taskExecutor,
            ScenarioStorage scenarioStorage,
            SchedulersStateRepository schedulersStateRepository,
            CommonSchedulerManager manager,
            CommonSchedulersMetrics metrics) {
        super(schedulersStateRepository);
        this.taskExecutor = taskExecutor;
        this.scenarioStorage = scenarioStorage;
        this.manager = manager;
        this.metrics = metrics;
    }

    @Scheduled(fixedDelayString = "${app.scheduler.common-database-scan.fixed-delay}")
    @Transactional
    public void ahahah() {
        SchedulersStateDto stateDto = getLastProcessedTime();

        if (!stateDto.lastTrySuccess()) {
            try {
                Thread.sleep(WAIT_IF_NEEDED_MS);
            } catch (InterruptedException ignored) {
                return;
            }
        }

        Instant from = stateDto.fetchTime();
        Instant to = Instant.now().plus(SECONDS_TO_EXTRA_SCAN, ChronoUnit.SECONDS);
        metrics.setTimeWindowValueSec(to.getEpochSecond() - from.getEpochSecond());

        if (from.isAfter(to)) {
            return;
        }

        AtomicBoolean cancelled = new AtomicBoolean(false);
        metrics.flushBatches();

        Iterator<List<ScenarioDto>> batchIterator =
                scenarioStorage.iterateScenariosInBatches(from, to, BATCH_SIZE, cancelled);
        while (batchIterator.hasNext()) {
            List<ScenarioDto> scenarios = batchIterator.next();

            try {
                taskExecutor.execute(() -> manager.handle(scenarios));
                metrics.incProcessedItems(scenarios.size());
                metrics.incBatches();
            } catch (RejectedExecutionException e) {
                cancelled.set(true);
                to = scenarios.stream()
                        .map(ScenarioDto::getFirstTimeToActivate)
                        .reduce(to, (a, b) -> a.isBefore(b) ? a : b);
                break;
            }
        }

        saveLastProcessedTime(to, !cancelled.get());
    }

    @Scheduled(fixedDelayString = "${app.scheduler.common-database-scan-checker.fixed-delay}")
    @Transactional
    public void checker() {
        SchedulersStateDto stateDto = getLastProcessedTime();

        Instant from = stateDto.fetchTime().minus(100, ChronoUnit.SECONDS);
        Instant to = stateDto.fetchTime().minus(20, ChronoUnit.SECONDS);

        Iterator<List<ScenarioDto>> batchIterator =
                scenarioStorage.iterateScenariosInBatches(from, to, BATCH_SIZE, new AtomicBoolean(false));

        while (batchIterator.hasNext()) {
            List<ScenarioDto> scenarios = batchIterator.next();
            manager.handleLostPart(scenarios);
        }
    }

    @Override
    protected Long getSchedulerId() {
        return SCHEDULER_ID;
    }
}
