package ru.hse.mmstr_project.se.service.antispam;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.kafka.dto.MetaRequestDto;
import ru.hse.mmstr_project.se.kafka.dto.TgBotRequestDto;
import ru.hse.mmstr_project.se.service.kafka.producer.MetaResponseService;
import ru.hse.mmstr_project.se.service.meta.EntityType;
import ru.hse.mmstr_project.se.service.meta.FunctionType;
import ru.hse.mmstr_project.se.service.storage.ScenarioStorage;
import ru.hse.mmstr_project.se.spam_detector.SpamDetectorManager;
import ru.hse.mmstr_project.se.storage.common.dto.ScenarioDto;

import java.util.List;
import java.util.Optional;

@Component
public class AntispamService {

    private final ScenarioStorage scenarioStorage;
    private final SpamDetectorManager spamDetectorManager;
    private final MetaResponseService responser;

    public AntispamService(
            ScenarioStorage scenarioStorage,
            SpamDetectorManager spamDetectorManager,
            MetaResponseService responser) {
        this.scenarioStorage = scenarioStorage;
        this.spamDetectorManager = spamDetectorManager;
        this.responser = responser;
    }

    public void handle(List<MetaRequestDto> requests) {
        requests.stream().parallel().forEach(this::handleOne);
    }

    @Transactional
    protected void handleOne(MetaRequestDto request) {
        if (!request.entityType().equals(EntityType.SCENARIO) && !request.functionType().equals(FunctionType.UPDATE)) {
            return;
        }
        List<ScenarioDto> scenarioDtoO = Optional.ofNullable(request.scenarioDto()).orElse(List.of());
        if (scenarioDtoO.isEmpty()) {
            return;
        }
        ScenarioDto scenarioDto = scenarioDtoO.getFirst();

        Optional<String> text = Optional.ofNullable(scenarioDto.getText());
        if (text.isEmpty()) {
            return;
        }
        boolean ok = !spamDetectorManager.isSpam(text.get());

        Optional<List<ScenarioDto>> scenariosO = Optional.ofNullable(scenarioDto.getUuid())
                .map(scenarioStorage::findAllByUuid)
                .or(() -> Optional.of(scenarioDto.getName())
                        .map(name -> scenarioStorage.findAllByClientIdAndName(request.chatId(), name)));
        if (scenariosO.isEmpty()) {
            responser.sendMessage(new TgBotRequestDto("Антиспам сервис: Не указан идентификатор сценариев, имя или uuid. Переотправьте запрос на изменение контента", request.chatId()));
            return;
        }

        List<ScenarioDto> scenarioDtos = scenariosO.get().stream()
                .map(it -> it.toBuilder()
                        .okFromAntispam(ok)
                        .build())
                .toList();
        if (scenarioDtos.isEmpty()) {
            responser.sendMessage(new TgBotRequestDto("Антиспам сервис: Не найдены сценарии по указанному идентификатору. Переотправьте запрос на изменение контента", request.chatId()));
            return;
        }

        scenarioStorage.saveAll(scenarioDtos);
        if (!ok) {
            responser.sendMessage(new TgBotRequestDto("Антиспам сервис: ты не пройдешь {" + scenarioDtos.getFirst().getUuid() + "}", request.chatId()));
        }
    }
}
