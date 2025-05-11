package ru.hse.mmstr_project.se.service.meta.handlers.impl;

import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.kafka.dto.MetaRequestDto;
import ru.hse.mmstr_project.se.service.meta.EntityType;
import ru.hse.mmstr_project.se.service.meta.FunctionType;
import ru.hse.mmstr_project.se.service.meta.MessageType;
import ru.hse.mmstr_project.se.service.meta.handlers.MetaRequestHandler;
import ru.hse.mmstr_project.se.service.storage.ClientStorage;
import ru.hse.mmstr_project.se.storage.common.dto.ClientDto;

import java.util.Optional;

@Component
public class ReadFriendHandlerImpl implements MetaRequestHandler {

    private final ClientStorage clientStorage;

    public ReadFriendHandlerImpl(ClientStorage clientStorage) {
        this.clientStorage = clientStorage;
    }

    @Override
    public Optional<String> handle(MetaRequestDto requestDto) {
        Optional<ClientDto> dto = clientStorage.findByChatId(requestDto.chatId());
        if (dto.isEmpty()) {
            return Optional.of("Ваш аккаунт не найден, попробуйте /start");
        }
        StringBuilder response = new StringBuilder();

        ClientDto clientDto = dto.get();
        clientDto.getListOfFriends().forEach(it -> response.append(it.toBeautyString()).append("\n\n"));

        response.append("Список айди всех ваших контактов:\n")
                .append("`")
                .append(String.join(" ", clientDto.getListOfFriends().stream().map(it -> it.getId().toString()).toList()))
                .append("`");

        return Optional.of(response.toString());
    }

    @Override
    public MessageType getMessageType() {
        return new MessageType(EntityType.CLIENT_FRIEND, FunctionType.READ);
    }
}
