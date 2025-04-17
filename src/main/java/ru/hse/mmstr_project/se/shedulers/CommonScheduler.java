package ru.hse.mmstr_project.se.shedulers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.hse.mmstr_project.se.storage.common.entity.Scenario;
import ru.hse.mmstr_project.se.storage.common.entity.system.SchedulersState;
import ru.hse.mmstr_project.se.storage.common.mapper.ClientMapper;
import ru.hse.mmstr_project.se.storage.common.repository.ScenarioRepository;
import ru.hse.mmstr_project.se.storage.common.repository.system.SchedulersStateRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

@Component
public class CommonScheduler {

    private static final String SCHEDULER_NAME = "CommonScheduler";
    private static final int SECONDS_TO_SCAN = 10;

    private final Executor taskExecutor;
    private final ScenarioRepository scenarioRepository;
    private final SchedulersStateRepository schedulersStateRepository;
    private final ClientMapper clientMapper;

    public CommonScheduler(
            @Qualifier("taskExecutor") Executor taskExecutor,
            ScenarioRepository scenarioRepository,
            SchedulersStateRepository schedulersStateRepository,
            ClientMapper clientMapper) {
        this.taskExecutor = taskExecutor;
        this.scenarioRepository = scenarioRepository;
        this.schedulersStateRepository = schedulersStateRepository;
        this.clientMapper = clientMapper;
    }

    @Scheduled(fixedDelayString = "${app.scheduler.common-database-scan.fixed-delay}")
    @Transactional
    public void ahahah() {
        long from = getLastProcessedTime();
        long to = Instant.now().plus(SECONDS_TO_SCAN, ChronoUnit.SECONDS).toEpochMilli();

        if (from >= to) {
            return;
        }

        try (Stream<Scenario> stream = scenarioRepository.streamScenariosInTimeRange(
                LocalDateTime.from(Instant.ofEpochMilli(from)),
                LocalDateTime.from(Instant.ofEpochMilli(to)))) {
            stream.map(clientMapper::toDto).forEach(a -> taskExecutor.execute(() -> System.out.println(a)));
        }
        saveLastProcessedTime(to);
    }

    @Transactional
    protected void saveLastProcessedTime(long time) {
        schedulersStateRepository.save(new SchedulersState(SCHEDULER_NAME, time));
    }

    @Transactional(readOnly = true)
    protected long getLastProcessedTime() {
        return schedulersStateRepository.findById(SCHEDULER_NAME)
                .map(SchedulersState::getFetchTime)
                .orElse(Instant.now().toEpochMilli());
    }
}
