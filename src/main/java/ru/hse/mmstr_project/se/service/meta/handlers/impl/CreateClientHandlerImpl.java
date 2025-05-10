package ru.hse.mmstr_project.se.service.meta.handlers.impl;

import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.kafka.dto.MetaRequestDto;
import ru.hse.mmstr_project.se.service.meta.EntityType;
import ru.hse.mmstr_project.se.service.meta.FunctionType;
import ru.hse.mmstr_project.se.service.meta.MessageType;
import ru.hse.mmstr_project.se.service.meta.handlers.MetaRequestHandler;
import ru.hse.mmstr_project.se.service.storage.ClientStorage;
import ru.hse.mmstr_project.se.storage.common.dto.CreateClientDto;

import java.util.List;
import java.util.Optional;

@Component
public class CreateClientHandlerImpl implements MetaRequestHandler {

    private final ClientStorage clientStorage;

    public CreateClientHandlerImpl(ClientStorage clientStorage) {
        this.clientStorage = clientStorage;
    }

    @Override
    public Optional<String> handle(MetaRequestDto requestDto) {
        try {
            requestDto.clientDto()
                    .map(it -> new CreateClientDto(
                            it.getName(),
                            it.getTelegramId(),
                            it.getChatId(),
                            List.of()))
                    .ifPresent(clientStorage::save);
        } catch (Exception e) {
            return Optional.of("Невозможно создать пользователя, возможно уже создан");
        }

        return Optional.empty();
    }

    @Override
    public MessageType getMessageType() {
        return new MessageType(EntityType.CLIENT, FunctionType.CREATE);
    }
}
