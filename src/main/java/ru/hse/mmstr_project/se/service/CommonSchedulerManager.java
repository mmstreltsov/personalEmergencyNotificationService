package ru.hse.mmstr_project.se.service;

import org.springframework.stereotype.Service;
import ru.hse.mmstr_project.se.service.storage.ClientStorage;
import ru.hse.mmstr_project.se.storage.common.dto.ClientDto;
import ru.hse.mmstr_project.se.storage.common.dto.ScenarioDto;
import ru.hse.mmstr_project.se.storage.fast_storage.dto.IncidentMetadataDto;
import ru.hse.mmstr_project.se.storage.fast_storage.repository.RedisItemRepository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CommonSchedulerManager {

    private final RedisItemRepository redisItemRepository;
    private final ClientStorage clientStorage;

    public CommonSchedulerManager(
            RedisItemRepository redisItemRepository,
            ClientStorage clientStorage) {
        this.redisItemRepository = redisItemRepository;
        this.clientStorage = clientStorage;
    }

    public void handle(Collection<ScenarioDto> scenarios) {
        List<ScenarioDto> processedScenarios = scenarios.stream()
                .filter(ScenarioDto::getOkFromAntispam)
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

        redisItemRepository.saveAll(data);

        // add notify to main user
    }
}
