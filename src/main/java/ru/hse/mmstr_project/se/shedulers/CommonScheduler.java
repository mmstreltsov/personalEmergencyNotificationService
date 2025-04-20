package ru.hse.mmstr_project.se.shedulers;

import com.google.common.collect.Iterators;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.hse.mmstr_project.se.storage.common.dto.CreateScenarioDto;
import ru.hse.mmstr_project.se.storage.common.dto.ScenarioDto;
import ru.hse.mmstr_project.se.storage.common.entity.Scenario;
import ru.hse.mmstr_project.se.storage.common.entity.system.SchedulersState;
import ru.hse.mmstr_project.se.storage.common.mapper.ClientMapper;
import ru.hse.mmstr_project.se.storage.common.repository.ScenarioRepository;
import ru.hse.mmstr_project.se.storage.common.repository.system.SchedulersStateRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

@Component
public class CommonScheduler {

    private static final String SCHEDULER_NAME = "CommonScheduler";
    private static final int SECONDS_TO_SCAN = 10;
    private static final int BATCH_SIZE = 128;

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
        tempI();

        long from = getLastProcessedTime();
        long to = Instant.now().plus(SECONDS_TO_SCAN, ChronoUnit.SECONDS).toEpochMilli();

        if (from >= to) {
            return;
        }

        try (Stream<Scenario> stream = scenarioRepository.streamScenariosInTimeRange(
                LocalDateTime.from(Instant.ofEpochMilli(from)),
                LocalDateTime.from(Instant.ofEpochMilli(to)))) {


            Iterators.partition(stream.iterator(), BATCH_SIZE)
                    .forEachRemaining(scenarios -> {
                        List<ScenarioDto> scenarioDtos = scenarios.stream().map(clientMapper::toDto).toList();
                        // do smth
                        scenarioDtos.forEach(a -> taskExecutor.execute(() -> System.out.println(a)));

                        updateObjectsToNextPing(scenarioDtos);
                    });
        }
        saveLastProcessedTime(to);
    }

    private void tempI() {
        for (int i = 0; i < 7; i++) {
            scenarioRepository.save(clientMapper.toEntity(new CreateScenarioDto(
                    "aahahhahahah",
                    -1L,
                    List.of(),
                    LocalDateTime.now().plus(7 + 11 * i, ChronoUnit.SECONDS),
                    List.of(LocalDateTime.now().plus(7 + 11 * i, ChronoUnit.SECONDS), LocalDateTime.now().plus(22 + 10 * i, ChronoUnit.SECONDS)),
                    -1,
                    true,
                    "heh"
            )));

        }
    }

    private void updateObjectsToNextPing(List<ScenarioDto> scenarios) {
        scenarios.forEach(scenarioDto -> {
            LocalDateTime nextTime = scenarioDto
                    .getListTimesToActivate()
                    .stream()
                    .filter(it -> it.isAfter(scenarioDto.getFirstTimeToActivate()))
                    .reduce(scenarioDto.getFirstTimeToActivate(), (a, b) -> a.isBefore(b) ? a : b);
            scenarioDto.setFirstTimeToActivate(nextTime);
        });
        scenarioRepository.saveAll(scenarios.stream().map(clientMapper::toEntity).toList());
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
