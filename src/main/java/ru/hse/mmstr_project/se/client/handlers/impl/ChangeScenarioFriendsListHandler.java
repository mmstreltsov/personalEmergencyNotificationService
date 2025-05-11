package ru.hse.mmstr_project.se.client.handlers.impl;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hse.mmstr_project.se.client.handlers.CommandHandler;
import ru.hse.mmstr_project.se.kafka.dto.MetaRequestDto;
import ru.hse.mmstr_project.se.service.kafka.producer.MetaRequestService;
import ru.hse.mmstr_project.se.service.meta.EntityType;
import ru.hse.mmstr_project.se.service.meta.FunctionType;
import ru.hse.mmstr_project.se.storage.common.dto.ScenarioDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ChangeScenarioFriendsListHandler implements CommandHandler {

    private final MetaRequestService metaRequestService;

    public ChangeScenarioFriendsListHandler(MetaRequestService metaRequestService) {
        this.metaRequestService = metaRequestService;
    }

    @Override
    public Optional<String> handle(String args, Long chatId, Message message) {
        Optional<String> idO = getArg(args, 0);
        if (idO.isEmpty()) {
            return Optional.of("Предоставьте айди сценария");
        }
        String id = idO.get();

        ScenarioDto.Builder builder = ScenarioDto.builder();
        try {
            UUID uuid = UUID.fromString(id);
            builder.uuid(uuid);
        } catch (Exception ignored) {
            builder.name(id);
        }
        ScenarioDto scenarioDto = builder.friendsIds(new ArrayList<>()).build();

        for (int i = 1; ; i++) {
            Optional<String> arg = getArg(args, i);
            if (arg.isEmpty()) {
                break;
            }
            scenarioDto.getFriendsIds().add(Long.parseLong(arg.get()));
        }

        metaRequestService.sendMessage(new MetaRequestDto(
                FunctionType.UPDATE,
                EntityType.SCENARIO,
                chatId,
                Optional.empty(),
                Optional.empty(),
                List.of(scenarioDto)));
        return Optional.empty();
    }

    @Override
    public String getCommand() {
        return "/set_contacts_to_scenario";
    }
}
