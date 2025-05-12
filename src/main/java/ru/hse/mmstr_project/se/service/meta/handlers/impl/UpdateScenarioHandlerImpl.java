package ru.hse.mmstr_project.se.service.meta.handlers.impl;

import jakarta.transaction.Transactional;
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
import java.time.temporal.ChronoUnit;
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

        if (scenarioDtos.isEmpty()) {
            return Optional.of("Ничего не делается");
        }

        if (scenarioDtos.size() == 1 && Objects.isNull(scenarioDtos.getFirst().getFirstTimeToActivate())) {
            return handleOne(requestDto, scenarioDtos.getFirst());
        }
        return handleMultipleRows(requestDto, scenarioDtos);
    }

    private Optional<String> handleOne(MetaRequestDto requestDto, ScenarioDto scenarioDto) {
        List<ScenarioDto> scenario = findAllByScenario(scenarioDto, requestDto.chatId());
        if (scenario.isEmpty()) {
            return Optional.of("Сценарий не найден");
        }

        scenarioStorage.saveAll(scenario.stream().map(it -> updating(it, scenarioDto)).toList());
        return Optional.empty();
    }

    protected Optional<String> handleMultipleRows(MetaRequestDto requestDto, List<ScenarioDto> scenarioDtos) {
        List<ScenarioDto> scenarios = findAllByScenario(scenarioDtos.getFirst(), requestDto.chatId());
        if (scenarios.isEmpty()) {
            return Optional.of("Сценарий не найден");
        }

        ScenarioDto scenario = scenarios.getFirst();
        List<CreateScenarioDto> result = scenarioDtos.stream()
                .map(it -> updating(scenario, it))
                .map(CreateScenarioDto::new)
                .toList();
        if (!validateTimings(result)) {
            return Optional.of("Интервалы между проверками меньше 5 минут. Увеличьте интервалы для сохранения");
        }

        saveAndDelete(result, scenarios.stream().map(ScenarioDto::getId).toList());
        return Optional.empty();
    }

    private static boolean validateTimings(List<CreateScenarioDto> scenarios) {
        List<Instant> list = scenarios.stream().map(CreateScenarioDto::getFirstTimeToActivate)
                .sorted((a, b) -> a.equals(b) ? 0 : a.isAfter(b) ? 1 : -1)
                .toList();
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i).plus(295, ChronoUnit.SECONDS).isAfter(list.get(i + 1))) {
                return false;
            }
        }
        return true;
    }

    private List<ScenarioDto> findAllByScenario(ScenarioDto scenarioDto, Long chatId) {
        if (Objects.nonNull(scenarioDto.getName()) && Objects.nonNull(scenarioDto.getUuid())) {
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

    protected void saveAndDelete(List<CreateScenarioDto> scenarioDtos, List<Long> toDelete) {
        if (!toDelete.isEmpty()) {
            scenarioStorage.deleteByIds(toDelete);
        }
        scenarioStorage.saveAllCreatingDto(scenarioDtos);
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
