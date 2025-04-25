package ru.hse.mmstr_project.se.shedulers;

import com.google.common.collect.Iterators;
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
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

@Component
public class CommonScheduler extends AbstractScheduler {

    private static final long SCHEDULER_ID = 1;
    private static final int SECONDS_TO_EXTRA_SCAN = 10;
    private static final int BATCH_SIZE = 128;
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
        Instant from = getLastProcessedTime();
        Instant to = Instant.now().plus(SECONDS_TO_EXTRA_SCAN, ChronoUnit.SECONDS);
        metrics.setTimeWindowValueSec(to.getEpochSecond() - from.getEpochSecond());

        if (!from.isBefore(to)) {
            return;
        }

        try (Stream<ScenarioDto> stream = scenarioStorage.streamScenariosInTimeRange(from, to)) {
            Iterators.partition(stream.iterator(), BATCH_SIZE)
                    .forEachRemaining(scenarios -> {
                        taskExecutor.execute(() -> manager.handle(scenarios));
                        updateObjectsToNextPing(scenarios, to.plus(1, ChronoUnit.MILLIS));
                        metrics.inc(scenarios.size());
                    });
        }
        saveLastProcessedTime(to);
    }

    @Transactional
    protected void updateObjectsToNextPing(List<ScenarioDto> scenarios, Instant minimalValue) {
        List<ScenarioDto> dtos = scenarios.stream().map(scenarioDto -> {
            Instant nextTime = scenarioDto
                    .getListTimesToActivate()
                    .stream()
                    .filter(it -> it.isAfter(scenarioDto.getFirstTimeToActivate()))
                    .reduce(NEVER, (a, b) -> a.isBefore(b) ? a : b);
            return scenarioDto.toBuilder()
                    .firstTimeToActivate(nextTime.isAfter(minimalValue) ? nextTime : minimalValue)
                    .build();
        }).toList();
        scenarioStorage.saveAll(dtos);
    }

    @Override
    protected Long getSchedulerId() {
        return SCHEDULER_ID;
    }
}
