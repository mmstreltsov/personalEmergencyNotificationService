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
        Optional<String> text = request.scenarioDto().map(ScenarioDto::getText);
        if (text.isEmpty()) {
            return;
        }

        Optional<String> name = request.scenarioDto().map(ScenarioDto::getName);
        if (name.isEmpty()) {
            responser.sendMessage(new TgBotRequestDto("Антиспам сервис: Не указан идентификатор сценариев, имя. Переотправьте запрос с указанием имени сценария", request.chatId()));
            return;
        }

        boolean ok = !spamDetectorManager.isSpam(text.get());
        List<ScenarioDto> clientScenarios = scenarioStorage.findAllByClientIdAndName(request.chatId(), name.get());

        List<ScenarioDto> scenarioDtos = clientScenarios.stream()
                .map(it -> it.toBuilder()
                        .okFromAntispam(ok)
                        .build())
                .toList();
        scenarioStorage.saveAll(scenarioDtos);

        if (!ok) {
            responser.sendMessage(new TgBotRequestDto("Антиспам сервис: ты не пройдешь {" + name.get() + "}", request.chatId()));
        }
    }
}
