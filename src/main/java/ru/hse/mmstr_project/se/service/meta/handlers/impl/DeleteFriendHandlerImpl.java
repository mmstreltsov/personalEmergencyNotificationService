package ru.hse.mmstr_project.se.service.meta.handlers.impl;

import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.kafka.dto.MetaRequestDto;
import ru.hse.mmstr_project.se.service.meta.EntityType;
import ru.hse.mmstr_project.se.service.meta.FunctionType;
import ru.hse.mmstr_project.se.service.meta.MessageType;
import ru.hse.mmstr_project.se.service.meta.handlers.MetaRequestHandler;
import ru.hse.mmstr_project.se.service.storage.ClientStorage;
import ru.hse.mmstr_project.se.storage.common.dto.ClientDto;
import ru.hse.mmstr_project.se.storage.common.dto.FriendDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DeleteFriendHandlerImpl implements MetaRequestHandler {

    private final ClientStorage clientStorage;

    public DeleteFriendHandlerImpl(ClientStorage clientStorage) {
        this.clientStorage = clientStorage;
    }

    @Override
    public Optional<String> handle(MetaRequestDto requestDto) {
        Optional<ClientDto> dto = clientStorage.findByChatId(requestDto.chatId());
        if (dto.isEmpty()) {
            return Optional.of("Ваш аккаунт не найден, попробуйте /start");
        }

        Optional<Integer> idO = requestDto.friendDto().map(FriendDto::getId);
        if (idO.isEmpty()) {
            return Optional.of("Не предоставлен айди контакта");
        }

        Integer id = idO.get();

        ClientDto clientDto = dto.get();
        List<FriendDto> friendDtos = new ArrayList<>(clientDto.getListOfFriends());

        List<FriendDto> newList = friendDtos.stream().filter(it -> !it.getId().equals(id)).toList();
        clientDto.setListOfFriends(newList);
        clientStorage.save(clientDto);

        return Optional.of("Контакт с айди {" + id + "} удален (без возможности восстановления)");
    }

    @Override
    public MessageType getMessageType() {
        return new MessageType(EntityType.CLIENT_FRIEND, FunctionType.DELETE);
    }
}
