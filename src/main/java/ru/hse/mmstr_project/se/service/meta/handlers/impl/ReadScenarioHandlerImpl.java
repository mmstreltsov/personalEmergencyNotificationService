package ru.hse.mmstr_project.se.service.meta.handlers.impl;

import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.kafka.dto.MetaRequestDto;
import ru.hse.mmstr_project.se.service.meta.EntityType;
import ru.hse.mmstr_project.se.service.meta.FunctionType;
import ru.hse.mmstr_project.se.service.meta.MessageType;
import ru.hse.mmstr_project.se.service.meta.handlers.MetaRequestHandler;
import ru.hse.mmstr_project.se.service.storage.ScenarioStorage;
import ru.hse.mmstr_project.se.storage.common.dto.ScenarioDto;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ReadScenarioHandlerImpl implements MetaRequestHandler {

    private final ScenarioStorage scenarioStorage;

    public ReadScenarioHandlerImpl(ScenarioStorage scenarioStorage) {
        this.scenarioStorage = scenarioStorage;
    }

    @Override
    public Optional<String> handle(MetaRequestDto requestDto) {
        List<ScenarioDto> scenarios = Optional.ofNullable(requestDto.scenarioDto())
                .filter(it -> !it.isEmpty())
                .map(List::getFirst)
                .flatMap(s -> Optional.ofNullable(s.getUuid())
                        .map(scenarioStorage::findAllByUuid)
                        .filter(it -> !it.isEmpty())
                        .or(() -> Optional.of(s.getName())
                                .map(name -> scenarioStorage.findAllByClientIdAndName(requestDto.chatId(), name)))
                )
                .orElse(scenarioStorage.findAllByClientId(requestDto.chatId()));

        Map<UUID, Set<Instant>> timesToActivate = new HashMap<>();
        Map<UUID, ScenarioDto> collect = scenarios.stream().collect(Collectors.toMap(
                ScenarioDto::getUuid,
                it -> {
                    Set<Instant> set = timesToActivate.getOrDefault(it.getUuid(), new HashSet<>());
                    set.add(it.getFirstTimeToActivateOrigin());
                    timesToActivate.put(it.getUuid(), set);

                    return it;
                },
                ((f, s) -> {
                    timesToActivate.get(f.getUuid()).add(s.getFirstTimeToActivateOrigin());
                    return f;
                })));

        StringBuilder response = new StringBuilder();
        collect.forEach((key, value) -> response
                .append(value.toBeautyString())
                .append(ScenarioDto.timesToString(timesToActivate.get(key)))
                .append("\n\n"));

        return Optional.of(response.toString());
    }

    @Override
    public MessageType getMessageType() {
        return new MessageType(EntityType.SCENARIO, FunctionType.READ);
    }
}
