package ru.hse.mmstr_project.se.client.handlers.impl;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hse.mmstr_project.se.client.handlers.CommandHandler;
import ru.hse.mmstr_project.se.kafka.dto.MetaRequestDto;
import ru.hse.mmstr_project.se.service.kafka.producer.MetaRequestService;
import ru.hse.mmstr_project.se.service.meta.EntityType;
import ru.hse.mmstr_project.se.service.meta.FunctionType;
import ru.hse.mmstr_project.se.storage.common.dto.ClientDto;

import java.util.List;
import java.util.Optional;

@Component
public class StartHandler implements CommandHandler {

    private final MetaRequestService metaRequestService;

    public StartHandler(MetaRequestService metaRequestService) {
        this.metaRequestService = metaRequestService;
    }

    @Override
    public Optional<String> handle(String ignored, Long chatId, Message message) {
        ClientDto clientDto = new ClientDto();

        clientDto.setChatId(chatId);
        clientDto.setName(message.getFrom().getFirstName() + " " + message.getFrom().getLastName());
        clientDto.setTelegramId(message.getFrom().getUserName());

        metaRequestService.sendMessage(new MetaRequestDto(
                FunctionType.CREATE,
                EntityType.CLIENT,
                chatId,
                Optional.of(clientDto),
                Optional.empty(),
                List.of()));
        return Optional.empty();
    }

    @Override
    public String getCommand() {
        return "/start";
    }
}
