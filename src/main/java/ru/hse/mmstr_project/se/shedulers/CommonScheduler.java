package ru.hse.mmstr_project.se.shedulers;

import com.google.common.collect.Iterators;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.hse.mmstr_project.se.service.CommonSchedulerManager;
import ru.hse.mmstr_project.se.service.storage.ScenarioStorage;
import ru.hse.mmstr_project.se.storage.common.dto.ScenarioDto;
import ru.hse.mmstr_project.se.storage.common.entity.system.SchedulersState;
import ru.hse.mmstr_project.se.storage.common.repository.system.SchedulersStateRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

@Component
public class CommonScheduler {

    private static final long SCHEDULER_ID = 1;
    private static final int SECONDS_TO_SCAN = 10;
    private static final int BATCH_SIZE = 128;
    private static final Instant NEVER = Instant.ofEpochSecond(9224318015999L); // max timestamp in postgres

    private final Executor taskExecutor;
    private final ScenarioStorage scenarioStorage;
    private final SchedulersStateRepository schedulersStateRepository;
    private final CommonSchedulerManager manager;

    public CommonScheduler(
            @Qualifier("taskExecutor") Executor taskExecutor,
            ScenarioStorage scenarioStorage,
            SchedulersStateRepository schedulersStateRepository,
            CommonSchedulerManager manager) {
        this.taskExecutor = taskExecutor;
        this.scenarioStorage = scenarioStorage;
        this.schedulersStateRepository = schedulersStateRepository;
        this.manager = manager;
    }

    @Scheduled(fixedDelayString = "${app.scheduler.common-database-scan.fixed-delay}")
    @Transactional
    public void ahahah() {
        long from = getLastProcessedTime();
        long to = Instant.now().plus(SECONDS_TO_SCAN, ChronoUnit.SECONDS).toEpochMilli();

        if (from >= to) {
            return;
        }

        try (Stream<ScenarioDto> stream = scenarioStorage.streamScenariosInTimeRange(
                Instant.ofEpochMilli(from),
                Instant.ofEpochMilli(to))) {

            Iterators.partition(stream.iterator(), BATCH_SIZE)
                    .forEachRemaining(scenarios -> {
                        taskExecutor.execute(() -> manager.handle(scenarios));
                        updateObjectsToNextPing(scenarios);
                    });
        }
        saveLastProcessedTime(to);
    }

    private void updateObjectsToNextPing(List<ScenarioDto> scenarios) {
        scenarios.forEach(scenarioDto -> {
            Instant nextTime = scenarioDto
                    .getListTimesToActivate()
                    .stream()
                    .filter(it -> it.isAfter(scenarioDto.getFirstTimeToActivate()))
                    .reduce(NEVER, (a, b) -> a.isBefore(b) ? a : b);
            scenarioDto.setFirstTimeToActivate(nextTime);
        });
        scenarioStorage.saveAll(scenarios);
    }

    @Transactional
    protected void saveLastProcessedTime(long time) {
        schedulersStateRepository.save(new SchedulersState(SCHEDULER_ID, time));
    }

    @Transactional(readOnly = true)
    protected long getLastProcessedTime() {
        return schedulersStateRepository.findById(SCHEDULER_ID)
                .map(SchedulersState::getFetchTime)
                .orElse(Instant.now().toEpochMilli());
    }
}
