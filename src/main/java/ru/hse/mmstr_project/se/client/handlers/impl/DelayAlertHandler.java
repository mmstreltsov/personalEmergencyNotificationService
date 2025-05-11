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

@Component
public class DelayAlertHandler implements CommandHandler {

    private final MetaRequestService metaRequestService;

    public DelayAlertHandler(MetaRequestService metaRequestService) {
        this.metaRequestService = metaRequestService;
    }

    @Override
    public Optional<String> handle(String args, Long chatId, Message message) {
        String idO = getArg(args, 0).orElse("5");

        metaRequestService.sendMessage(new MetaRequestDto(
                FunctionType.UPDATE,
                EntityType.ALERT,
                chatId,
                Optional.empty(),
                Optional.empty(),
                List.of(new ScenarioDto().toBuilder().allowedDelayAfterPing(Integer.parseInt(idO)).build())));
        return Optional.empty();
    }

    @Override
    public String getCommand() {
        return "/delay";
    }
}
