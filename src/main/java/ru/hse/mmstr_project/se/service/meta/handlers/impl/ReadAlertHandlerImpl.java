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
public class ReadAlertHandlerImpl implements MetaRequestHandler {

    private final ScenarioStorage scenarioStorage;

    public ReadAlertHandlerImpl(ScenarioStorage scenarioStorage) {
        this.scenarioStorage = scenarioStorage;
    }

    @Override
    public Optional<String> handle(MetaRequestDto requestDto) {
        Optional<ScenarioDto> nextScenario = scenarioStorage.findNextAlertByChatId(requestDto.chatId());
        return nextScenario.map(scenarioDto -> scenarioDto.toBeautyString() + ScenarioDto.timesToString(List.of(scenarioDto.getFirstTimeToActivate())))
                .or(() -> Optional.of("Не нашлось сценария для /skip-next"));
    }

    @Override
    public MessageType getMessageType() {
        return new MessageType(EntityType.ALERT, FunctionType.READ);
    }
}
