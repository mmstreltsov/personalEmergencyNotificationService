package ru.hse.mmstr_project.se.service.schedulers;

import com.google.common.collect.Iterators;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hse.mmstr_project.se.kafka.dto.PingerDto;
import ru.hse.mmstr_project.se.service.kafka.producer.PingerService;
import ru.hse.mmstr_project.se.service.storage.ClientStorage;
import ru.hse.mmstr_project.se.service.storage.ScenarioStorage;
import ru.hse.mmstr_project.se.shedulers.metrics.CommonSchedulersMetrics;
import ru.hse.mmstr_project.se.storage.common.dto.ClientDto;
import ru.hse.mmstr_project.se.storage.common.dto.ScenarioDto;
import ru.hse.mmstr_project.se.storage.fast_storage.dto.IncidentMetadataDto;
import ru.hse.mmstr_project.se.storage.fast_storage.repository.RedisItemRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CommonSchedulerManager {

    private static final int BATCH_SIZE = 256;

    private final RedisItemRepository redisItemRepository;
    private final ClientStorage clientStorage;
    private final ScenarioStorage scenarioStorage;
    private final CommonSchedulersMetrics commonSchedulersMetrics;
    private final PingerService pingerService;

    public CommonSchedulerManager(
            RedisItemRepository redisItemRepository,
            ClientStorage clientStorage,
            ScenarioStorage scenarioStorage,
            CommonSchedulersMetrics commonSchedulersMetrics,
            PingerService pingerService) {
        this.redisItemRepository = redisItemRepository;
        this.clientStorage = clientStorage;
        this.scenarioStorage = scenarioStorage;
        this.commonSchedulersMetrics = commonSchedulersMetrics;
        this.pingerService = pingerService;
    }

    public void handle(Collection<ScenarioDto> scenarios) {
        commonSchedulersMetrics.measureRequest(() -> handleI(scenarios));
    }

    private void handleI(Collection<ScenarioDto> scenarios) {
        List<ScenarioDto> processedScenarios = scenarios.stream()
                .filter(it -> it.getOkFromAntispam() || it.getOkByHand())
                .toList();

        Set<Long> userIds = processedScenarios.stream().map(ScenarioDto::getClientId).collect(Collectors.toSet());
        Map<Long, ClientDto> idToClient = clientStorage.findAllByIds(userIds)
                .stream()
                .collect(Collectors.toMap(
                        ClientDto::getId,
                        Function.identity(),
                        (f, s) -> s));

        List<IncidentMetadataDto> data = scenarios.stream()
                .map(scenario -> Optional.ofNullable(idToClient.get(scenario.getClientId()))
                        .map(client -> IncidentMetadataDto.parse(scenario, client)))
                .flatMap(Optional::stream)
                .toList();

        Iterators.partition(data.iterator(), BATCH_SIZE).forEachRemaining(entities -> {
            entities.stream().map(PingerDto::cons).forEach(pingerService::sendMessage);
            redisItemRepository.saveAll(entities);
        });
    }

    @Transactional
    public void handleLostPart(Collection<ScenarioDto> scenarios) {
        Map<String, ScenarioDto> collect = scenarios.stream()
                .filter(it -> it.getOkFromAntispam() && it.getOkByHand())
                .collect(Collectors.toMap(
                        it -> it.getUuid().toString(),
                        it -> it,
                        (f, s) -> s));

        if (collect.isEmpty()) {
            return;
        }
        List<String> result = redisItemRepository.filterDuplicates(collect.keySet())
                .stream()
                .toList();
        if (result.isEmpty()) {
            return;
        }

        Set<String> alreadyInRedisToo = redisItemRepository.getEntitiesByIds(result).stream()
                .map(IncidentMetadataDto::id)
                .collect(Collectors.toSet());
        result = result.stream().filter(it -> !alreadyInRedisToo.contains(it)).toList();
        commonSchedulersMetrics.incLostItems(result.size());

        updateObjectsToNextPing(
                result.stream().map(collect::get).toList(),
                Instant.now().plus(31, ChronoUnit.SECONDS));
    }

    private void updateObjectsToNextPing(List<ScenarioDto> scenarios, Instant minimalValue) {
        List<ScenarioDto> dtos = scenarios.stream().map(scenarioDto -> {
            long delay = scenarioDto.getAllowedDelayAfterPing() -
                    (minimalValue.getEpochSecond() - scenarioDto.getFirstTimeToActivateOrigin().getEpochSecond());

            return scenarioDto.toBuilder()
                    .firstTimeToActivate(minimalValue)
                    .allowedDelayAfterPing(delay >= 0 ? (int) delay : 0)
                    .build();
        }).toList();
        if (dtos.isEmpty()) {
            return;
        }

        scenarioStorage.saveAll(dtos);
    }
}
