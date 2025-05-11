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
        List<ScenarioDto> scenario = findAllByScenario(scenarioDto, requestDto.chatId());
        scenarioStorage.saveAll(scenario.stream().map(it -> updating(it, scenarioDto)).toList());

        return Optional.empty();
    }

    protected Optional<String> handleMultiple(MetaRequestDto requestDto, List<ScenarioDto> scenarioDtos) {

        List<ScenarioDto> scenario = findAllByScenario(scenarioDtos.getFirst(), requestDto.chatId());

        List<ScenarioDto> dtosFromDb = new ArrayList<>(scenario);
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

        return Optional.of("Сценарию проставлено время срабатывания (в UTC): "
                + String.join(" ", result.stream().map(it -> it.getFirstTimeToActivate().toString()).toList()));
    }

    private List<ScenarioDto> findAllByScenario(ScenarioDto scenarioDto, Long chatId) {
        if (Objects.nonNull(scenarioDto.getName())) {
            if (!scenarioStorage.findAllByClientIdAndName(chatId, scenarioDto.getName()).isEmpty()) {
                throw new RuntimeException("Сценарий с указанным именем уже существует, пропускаю изменение");
            }
        }

        Optional<List<ScenarioDto>> scenariosO = Optional.ofNullable(scenarioDto.getUuid())
                .map(scenarioStorage::findAllByUuid)
                .filter(it -> !it.isEmpty())
                .or(() -> Optional.of(scenarioDto.getName())
                        .map(name -> scenarioStorage.findAllByClientIdAndName(chatId, name)));
        if (scenariosO.isEmpty()) {
            throw new RuntimeException("Не предоставлен uuid или name -- не могу найти сценарии");
        }

        List<ScenarioDto> result = scenariosO.get();
        if (!result.stream().map(ScenarioDto::getClientId).allMatch(it -> it.equals(chatId))) {
            throw new RuntimeException("Захвачены чужие сценарии, конфликт имен или айдишников. Попробуйте изменить name");
        }

        return result;
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
        Optional.ofNullable(toDb.getOkByHand()).ifPresent(builder::okByHand);

        return builder.build();
    }

    @Override
    public MessageType getMessageType() {
        return new MessageType(EntityType.SCENARIO, FunctionType.UPDATE);
    }
}
