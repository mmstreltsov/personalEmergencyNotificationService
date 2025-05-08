package ru.hse.mmstr_project.se.service.antispam;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.kafka.dto.MetaRequestDto;
import ru.hse.mmstr_project.se.service.meta.EntityType;
import ru.hse.mmstr_project.se.service.meta.FunctionType;
import ru.hse.mmstr_project.se.service.storage.ScenarioStorage;
import ru.hse.mmstr_project.se.spam_detector.SpamDetectorManager;
import ru.hse.mmstr_project.se.storage.common.dto.ScenarioDto;

import java.util.List;

@Component
public class AntispamService {

    private static final String FIELD_NAME = "name";

    private final ScenarioStorage scenarioStorage;
    private final SpamDetectorManager spamDetectorManager;

    public AntispamService(ScenarioStorage scenarioStorage, SpamDetectorManager spamDetectorManager) {
        this.scenarioStorage = scenarioStorage;
        this.spamDetectorManager = spamDetectorManager;
    }

    public void handle(List<MetaRequestDto> requests) {
        requests.stream().parallel().forEach(this::handleOne);
    }

    @Transactional
    protected void handleOne(MetaRequestDto request) {
        if (!request.entityType().equals(EntityType.SCENARIO) && !request.functionType().equals(FunctionType.UPDATE)) {
            return;
        }
        if (request.updateParams().isEmpty() || !request.updateParams().get().field().equalsIgnoreCase(FIELD_NAME)) {
            return;
        }

        boolean ok = !spamDetectorManager.isSpam(request.updateParams().get().field());
        List<ScenarioDto> clientScenarios =
                scenarioStorage.findAllByClientIdAndName(request.chatId(), request.updateParams().get().uniqueId());

        List<ScenarioDto> scenarioDtos = clientScenarios.stream()
                .map(it -> it.toBuilder()
                        .okFromAntispam(ok)
                        .build())
                .toList();
        scenarioStorage.saveAll(scenarioDtos);
    }
}
