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
import java.util.Objects;
import java.util.Optional;

@Component
public class UpdateScenarioHandlerImpl implements MetaRequestHandler {

    private final ScenarioStorage scenarioStorage;

    public UpdateScenarioHandlerImpl(ScenarioStorage scenarioStorage) {
        this.scenarioStorage = scenarioStorage;
    }

    @Override
    public Optional<String> handle(MetaRequestDto requestDto) {
        Optional<ScenarioDto> scenarioDtoO = requestDto.scenarioDto();
        if (scenarioDtoO.isEmpty()) {
            return Optional.of("Ничего не делается");
        }
        ScenarioDto scenarioDto = scenarioDtoO.get();

        if (Objects.nonNull(scenarioDto.getName())) {
            if (!scenarioStorage.findAllByClientIdAndName(requestDto.chatId(), scenarioDto.getName()).isEmpty()) {
                return Optional.of("Сценарий с указанным именем уже существует, пропускаю изменение");
            }
        }

        Optional<List<ScenarioDto>> scenariosO = Optional.ofNullable(scenarioDto.getUuid())
                .map(scenarioStorage::findAllByUuid)
                .or(() -> Optional.of(scenarioDto.getName())
                        .map(name -> scenarioStorage.findAllByClientIdAndName(requestDto.chatId(), name)));

        if (scenariosO.isEmpty()) {
            return Optional.of("Не предоставлен uuid или name -- не могу найти сценарии");
        }
        scenarioStorage.saveAll(scenariosO.get().stream().map(it -> updating(it, scenarioDto)).toList());

        return Optional.empty();
    }

    private ScenarioDto updating(ScenarioDto fromDb, ScenarioDto toDb) {
        ScenarioDto.Builder builder = fromDb.toBuilder();

        Optional.ofNullable(toDb.getName()).ifPresent(builder::name);
        Optional.ofNullable(toDb.getText()).ifPresent(builder::text);
        Optional.ofNullable(toDb.getFriendsIds()).ifPresent(builder::friendsIds);
        Optional.ofNullable(toDb.getFirstTimeToActivate()).ifPresent(builder::firstTimeToActivate);
        Optional.ofNullable(toDb.getFirstTimeToActivate()).ifPresent(builder::firstTimeToActivateOrigin);
        Optional.ofNullable(toDb.getAllowedDelayAfterPing()).ifPresent(builder::allowedDelayAfterPing);
        Optional.ofNullable(toDb.getTextToPing()).ifPresent(builder::textToPing);

        return builder.build();
    }


    @Override
    public MessageType getMessageType() {
        return new MessageType(EntityType.SCENARIO, FunctionType.UPDATE);
    }
}
