package ru.hse.mmstr_project.se.service.meta.handlers.impl;

import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.kafka.dto.MetaRequestDto;
import ru.hse.mmstr_project.se.service.meta.EntityType;
import ru.hse.mmstr_project.se.service.meta.FunctionType;
import ru.hse.mmstr_project.se.service.meta.MessageType;
import ru.hse.mmstr_project.se.service.meta.handlers.MetaRequestHandler;
import ru.hse.mmstr_project.se.service.storage.ScenarioStorage;
import ru.hse.mmstr_project.se.storage.common.dto.ScenarioDto;
import ru.hse.mmstr_project.se.storage.fast_storage.repository.RedisItemRepository;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Component
public class UpdateAlertHandlerImpl implements MetaRequestHandler {

    private final ScenarioStorage scenarioStorage;
    private final RedisItemRepository repository;

    public UpdateAlertHandlerImpl(
            ScenarioStorage scenarioStorage,
            RedisItemRepository repository) {
        this.scenarioStorage = scenarioStorage;
        this.repository = repository;
    }

    @Override
    public Optional<String> handle(MetaRequestDto requestDto) {
        if (Optional.ofNullable(requestDto.scenarioDto()).orElse(List.of()).isEmpty()) {
            Optional<ScenarioDto> lastScenario = scenarioStorage.findLastAlertByChatId(requestDto.chatId());
            if (lastScenario.isEmpty()) {
                return Optional.of("Ничего не делается");
            }

            repository.removeFromIndex(lastScenario.get().getUuid().toString());
            return Optional.of("Эскалация остановлена для сценария: " + lastScenario.get().getName());
        } else if (Optional.ofNullable(requestDto.scenarioDto().getFirst().getAllowedDelayAfterPing()).isPresent()) {
            Optional<ScenarioDto> nextScenario = scenarioStorage.findNextAlertByChatId(requestDto.chatId());
            if (nextScenario.isEmpty()) {
                return Optional.of("Не нашлось сценария для /delay");
            }

            Integer delay = requestDto.scenarioDto().getFirst().getAllowedDelayAfterPing();
            nextScenario.map(it -> it.toBuilder().firstTimeToActivate(it.getFirstTimeToActivate().plus(delay, ChronoUnit.MINUTES)).build())
                    .ifPresent(scenarioStorage::save);
        } else if (Optional.ofNullable(requestDto.scenarioDto().getFirst().getOkByHand()).orElse(false)) {
            Optional<ScenarioDto> nextScenario = scenarioStorage.findNextAlertByChatId(requestDto.chatId());
            if (nextScenario.isEmpty()) {
                return Optional.of("Не нашлось сценария для /skip-next");
            }

            nextScenario.map(it -> it.toBuilder().firstTimeToActivate(CreateScenarioHandlerImpl.NEVER).build())
                    .ifPresent(scenarioStorage::save);
        }

        return Optional.empty();
    }


    @Override
    public MessageType getMessageType() {
        return new MessageType(EntityType.ALERT, FunctionType.UPDATE);
    }
}
