package ru.hse.mmstr_project.se.client.handlers.impl;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hse.mmstr_project.se.client.handlers.CommandHandler;
import ru.hse.mmstr_project.se.kafka.dto.MetaRequestDto;
import ru.hse.mmstr_project.se.service.kafka.producer.MetaRequestService;
import ru.hse.mmstr_project.se.service.meta.EntityType;
import ru.hse.mmstr_project.se.service.meta.FunctionType;

import java.util.List;
import java.util.Optional;

@Component
public class GetNextAlertHandler implements CommandHandler {

    private final MetaRequestService metaRequestService;

    public GetNextAlertHandler(MetaRequestService metaRequestService) {
        this.metaRequestService = metaRequestService;
    }

    @Override
    public Optional<String> handle(String args, Long chatId, Message message) {
        metaRequestService.sendMessage(new MetaRequestDto(
                FunctionType.READ,
                EntityType.ALERT,
                chatId,
                Optional.empty(),
                Optional.empty(),
                List.of()));
        return Optional.empty();
    }

    @Override
    public String getCommand() {
        return "/get_next_alert";
    }
}
