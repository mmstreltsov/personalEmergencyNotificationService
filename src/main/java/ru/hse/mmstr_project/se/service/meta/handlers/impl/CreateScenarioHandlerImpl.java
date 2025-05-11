package ru.hse.mmstr_project.se.service.meta.handlers.impl;

import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.kafka.dto.MetaRequestDto;
import ru.hse.mmstr_project.se.service.meta.EntityType;
import ru.hse.mmstr_project.se.service.meta.FunctionType;
import ru.hse.mmstr_project.se.service.meta.MessageType;
import ru.hse.mmstr_project.se.service.meta.handlers.MetaRequestHandler;
import ru.hse.mmstr_project.se.service.storage.ScenarioStorage;
import ru.hse.mmstr_project.se.storage.common.dto.CreateScenarioDto;
import ru.hse.mmstr_project.se.storage.common.dto.ScenarioDto;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CreateScenarioHandlerImpl implements MetaRequestHandler {

    private static final Instant NEVER = Instant.ofEpochSecond(9224318015999L); // max timestamp in postgres

    public static final String DEFAULT_SCENARIO_NAME = "Scenario-" + Instant.now().getEpochSecond();
    public static final String DEFAULT_SCENARIO_TEXT = "Я в беде";
    public static final String DEFAULT_SCENARIO_PING = "С вами все хорошо? Отправьте /confirm для остановки эскалации";

    private final ScenarioStorage scenarioStorage;


    public CreateScenarioHandlerImpl(ScenarioStorage scenarioStorage) {
        this.scenarioStorage = scenarioStorage;
    }

    @Override
    public Optional<String> handle(MetaRequestDto requestDto) {

        CreateScenarioDto scenarioDto = getDefaultCreateScenarioDto(requestDto.chatId());
        requestDto.scenarioDto().map(ScenarioDto::getName).filter(it -> !it.isEmpty()).ifPresent(scenarioDto::setName);

        String id = scenarioStorage.save(scenarioDto);

        return Optional.of("Создан сценарий с id: " + id);
    }

    private CreateScenarioDto getDefaultCreateScenarioDto(Long chatId) {
        return new CreateScenarioDto(
                UUID.randomUUID(),
                DEFAULT_SCENARIO_NAME,
                DEFAULT_SCENARIO_TEXT,
                chatId,
                List.of(),
                NEVER,
                NEVER,
                1,
                false,
                false,
                DEFAULT_SCENARIO_PING);
    }

    @Override
    public MessageType getMessageType() {
        return new MessageType(EntityType.SCENARIO, FunctionType.CREATE);
    }
}
