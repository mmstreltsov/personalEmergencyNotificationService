package ru.hse.mmstr_project.se.client.handlers.impl;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hse.mmstr_project.se.client.handlers.CommandHandler;
import ru.hse.mmstr_project.se.kafka.dto.MetaRequestDto;
import ru.hse.mmstr_project.se.service.kafka.producer.MetaRequestService;
import ru.hse.mmstr_project.se.service.meta.EntityType;
import ru.hse.mmstr_project.se.service.meta.FunctionType;
import ru.hse.mmstr_project.se.storage.common.dto.ScenarioDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ChangeScenarioNameHandler implements CommandHandler {

    private final MetaRequestService metaRequestService;

    public ChangeScenarioNameHandler(MetaRequestService metaRequestService) {
        this.metaRequestService = metaRequestService;
    }

    @Override
    public Optional<String> handle(String args, Long chatId, Message message) {
        Optional<String> id = getArg(args, 0);
        if (id.isEmpty()) {
            return Optional.of("Предоставьте айди сценария");
        }

        Optional<String> nameO = getArg(args, 1);
        if (nameO.isEmpty()) {
            return Optional.of("Предоставьте имя сценария");
        }

        ScenarioDto scenarioDto = ScenarioDto.builder()
                .uuid(UUID.fromString(id.get().trim()))
                .name(nameO.get())
                .build();

        metaRequestService.sendMessage(new MetaRequestDto(
                FunctionType.UPDATE,
                EntityType.SCENARIO,
                chatId,
                Optional.empty(),
                Optional.empty(),
                List.of(scenarioDto),
                false));
        return Optional.empty();
    }

    @Override
    public String getCommand() {
        return "/set_scenario_name";
    }
}
