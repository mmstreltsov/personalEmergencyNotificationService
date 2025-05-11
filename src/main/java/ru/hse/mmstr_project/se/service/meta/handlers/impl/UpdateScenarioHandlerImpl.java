package ru.hse.mmstr_project.se.service.meta.handlers.impl;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.kafka.dto.MetaRequestDto;
import ru.hse.mmstr_project.se.service.meta.EntityType;
import ru.hse.mmstr_project.se.service.meta.FunctionType;
import ru.hse.mmstr_project.se.service.meta.MessageType;
import ru.hse.mmstr_project.se.service.meta.handlers.MetaRequestHandler;
import ru.hse.mmstr_project.se.service.storage.ScenarioStorage;
import ru.hse.mmstr_project.se.storage.common.dto.ScenarioDto;

import java.util.ArrayList;
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
    @Transactional
    public Optional<String> handle(MetaRequestDto requestDto) {
        List<ScenarioDto> scenarioDtos = Optional.ofNullable(requestDto.scenarioDto()).orElse(List.of());

        return switch (scenarioDtos.size()) {
            case 0 -> Optional.of("Ничего не делается");
            case 1 -> handleOne(requestDto, scenarioDtos.getFirst());
            default -> handleMultiple(requestDto, scenarioDtos);
        };
    }

    private Optional<String> handleOne(MetaRequestDto requestDto, ScenarioDto scenarioDto) {
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

    protected Optional<String> handleMultiple(MetaRequestDto requestDto, List<ScenarioDto> scenarioDtos) {
        if (Objects.nonNull(scenarioDtos.getFirst().getName())) {
            if (!scenarioStorage.findAllByClientIdAndName(requestDto.chatId(), scenarioDtos.getFirst().getName()).isEmpty()) {
                return Optional.of("Сценарий с указанным именем уже существует, пропускаю изменение");
            }
        }

        Optional<List<ScenarioDto>> scenariosO = Optional.ofNullable(scenarioDtos.getFirst().getUuid())
                .map(scenarioStorage::findAllByUuid)
                .or(() -> Optional.of(scenarioDtos.getFirst().getName())
                        .map(name -> scenarioStorage.findAllByClientIdAndName(requestDto.chatId(), name)));
        if (scenariosO.isEmpty()) {
            return Optional.of("Не предоставлен uuid или name -- не могу найти сценарии");
        }

        List<ScenarioDto> dtosFromDb = new ArrayList<>(scenariosO.get());
        List<Long> toDelete = new ArrayList<>();

        while (dtosFromDb.size() < scenarioDtos.size()) {
            dtosFromDb.add(dtosFromDb.getFirst());
        }
        if (dtosFromDb.size() > scenarioDtos.size()) {
            dtosFromDb.stream().map(ScenarioDto::getId).limit(scenarioDtos.size() - dtosFromDb.size()).forEach(toDelete::add);
        }

        List<ScenarioDto> result = new ArrayList<>(dtosFromDb);
        for (int i = 0; i < scenarioDtos.size(); i++) {
            result.add(updating(dtosFromDb.get(i), scenarioDtos.get(i)));
        }
        saveAndDelete(result, toDelete);

        return Optional.empty();
    }

    protected void saveAndDelete(List<ScenarioDto> scenarioDtos, List<Long> toDelete) {
        if (!toDelete.isEmpty()) {
            scenarioStorage.deleteByIds(toDelete);
        }
        scenarioStorage.saveAll(scenarioDtos);
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
