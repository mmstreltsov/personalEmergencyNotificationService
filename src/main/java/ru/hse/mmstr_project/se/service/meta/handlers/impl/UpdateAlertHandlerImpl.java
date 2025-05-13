package ru.hse.mmstr_project.se.service.meta.handlers.impl;

import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.kafka.dto.MetaRequestDto;
import ru.hse.mmstr_project.se.service.meta.EntityType;
import ru.hse.mmstr_project.se.service.meta.FunctionType;
import ru.hse.mmstr_project.se.service.meta.MessageType;
import ru.hse.mmstr_project.se.service.meta.handlers.MetaRequestHandler;
import ru.hse.mmstr_project.se.service.sender.SenderService;
import ru.hse.mmstr_project.se.service.storage.ClientStorage;
import ru.hse.mmstr_project.se.service.storage.ScenarioStorage;
import ru.hse.mmstr_project.se.storage.common.dto.ScenarioDto;
import ru.hse.mmstr_project.se.storage.fast_storage.dto.IncidentMetadataDto;
import ru.hse.mmstr_project.se.storage.fast_storage.repository.RedisItemRepository;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Component
public class UpdateAlertHandlerImpl implements MetaRequestHandler {

    private static final String TEXT_FOR_LATE_OK = "Ложная тревога, Ваш друг %s прислал подтверждение что все хорошо.";
    private static final String TEXT_FOR_LATE_DELAY = "Ложная тревога, Ваш друг %s отодвинул проверку.";

    private final ScenarioStorage scenarioStorage;
    private final ClientStorage clientStorage;
    private final RedisItemRepository repository;
    private final SenderService senderService;

    public UpdateAlertHandlerImpl(
            ScenarioStorage scenarioStorage,
            ClientStorage clientStorage,
            RedisItemRepository repository,
            SenderService senderService) {
        this.scenarioStorage = scenarioStorage;
        this.clientStorage = clientStorage;
        this.repository = repository;
        this.senderService = senderService;
    }

    @Override
    public Optional<String> handle(MetaRequestDto requestDto) {
        if (Optional.ofNullable(requestDto.scenarioDto()).orElse(List.of()).isEmpty()) {
            Optional<ScenarioDto> lastScenarioO = scenarioStorage.findLastAlertByChatId(requestDto.chatId());
            if (lastScenarioO.isEmpty()) {
                return Optional.of("Ничего не делается");
            }
            ScenarioDto lastScenario = lastScenarioO.get();
            String keyInRedis = lastScenario.getUuid().toString();

            repository.remove(keyInRedis);
            if (repository.isInDuplicates(keyInRedis)) {
                lateOk(lastScenario, TEXT_FOR_LATE_OK);
            }
            repository.saveConfirm(keyInRedis);

            return Optional.of("Эскалация остановлена для сценария: " + lastScenario.getName());
        } else if (Optional.ofNullable(requestDto.scenarioDto().getFirst().getAllowedDelayAfterPing()).isPresent()) {
            Optional<ScenarioDto> nextScenario = scenarioStorage.findNextAlertByChatId(requestDto.chatId());
            if (nextScenario.isEmpty()) {
                return Optional.of("Не нашлось сценария для /delay");
            }

            Integer delay = requestDto.scenarioDto().getFirst().getAllowedDelayAfterPing();
            nextScenario.map(it -> it.toBuilder()
                            .firstTimeToActivate(it.getFirstTimeToActivate().plus(delay, ChronoUnit.MINUTES))
                            .build())
                    .ifPresent(scenarioStorage::save);

            nextScenario.ifPresent(sc -> {
                String keyInRedis = sc.getUuid().toString();

                repository.remove(keyInRedis);
                if (repository.isInDuplicates(keyInRedis)) {
                    lateOk(sc, TEXT_FOR_LATE_DELAY);
                }
            });

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

    private void lateOk(ScenarioDto scenarioDto, String text) {
        clientStorage.findByChatId(scenarioDto.getClientId())
                .map(client -> {
                    ScenarioDto build = scenarioDto.toBuilder()
                            .text(String.format(text, client.getName()))
                            .build();
                    return IncidentMetadataDto.parse(build, client);
                })
                .ifPresent(inc -> senderService.send(List.of(inc), false));
    }


    @Override
    public MessageType getMessageType() {
        return new MessageType(EntityType.ALERT, FunctionType.UPDATE);
    }
}
