package ru.hse.mmstr_project.se.service.meta.handlers.impl;

import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.kafka.dto.MetaRequestDto;
import ru.hse.mmstr_project.se.service.meta.EntityType;
import ru.hse.mmstr_project.se.service.meta.FunctionType;
import ru.hse.mmstr_project.se.service.meta.MessageType;
import ru.hse.mmstr_project.se.service.meta.handlers.MetaRequestHandler;
import ru.hse.mmstr_project.se.service.storage.ScenarioStorage;
import ru.hse.mmstr_project.se.storage.common.dto.ScenarioDto;

import java.util.List;
import java.util.Optional;

@Component
public class DeleteScenarioHandlerImpl implements MetaRequestHandler {

    private final ScenarioStorage scenarioStorage;

    public DeleteScenarioHandlerImpl(ScenarioStorage scenarioStorage) {
        this.scenarioStorage = scenarioStorage;
    }

    @Override
    public Optional<String> handle(MetaRequestDto requestDto) {
        List<ScenarioDto> scenarioDtos = Optional.ofNullable(requestDto.scenarioDto()).orElse(List.of());
        if (scenarioDtos.isEmpty()) {
            return Optional.empty();
        }

        Optional<List<ScenarioDto>> scenariosO = Optional.ofNullable(scenarioDtos.getFirst().getUuid())
                .map(scenarioStorage::findAllByUuid)
                .filter(it -> !it.isEmpty())
                .or(() -> Optional.of(scenarioDtos.getFirst().getName())
                        .map(name -> scenarioStorage.findAllByClientIdAndName(requestDto.chatId(), name)));

        if (scenariosO.isEmpty()) {
            return Optional.of("Не предоставлен uuid или name -- не могу найти сценарии");
        }

        List<ScenarioDto> result = scenariosO.get();
        if (result.isEmpty()) {
            return Optional.of("Не найден сценарий");
        }

        if (!result.stream().map(ScenarioDto::getClientId).allMatch(it -> it.equals(requestDto.chatId()))) {
            return Optional.of("Захвачены чужие сценарии, конфликт имен или айдишников");
        }

        scenarioStorage.deleteByIds(result.stream().map(ScenarioDto::getId).toList());

        return Optional.of("Удален сценарий с id: " + requestDto.chatId());
    }

    @Override
    public MessageType getMessageType() {
        return new MessageType(EntityType.SCENARIO, FunctionType.DELETE);
    }
}
