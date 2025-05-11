package ru.hse.mmstr_project.se.service.meta.handlers.impl;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.kafka.dto.MetaRequestDto;
import ru.hse.mmstr_project.se.service.meta.EntityType;
import ru.hse.mmstr_project.se.service.meta.FunctionType;
import ru.hse.mmstr_project.se.service.meta.MessageType;
import ru.hse.mmstr_project.se.service.meta.handlers.MetaRequestHandler;
import ru.hse.mmstr_project.se.service.storage.ClientStorage;
import ru.hse.mmstr_project.se.storage.common.dto.ClientDto;
import ru.hse.mmstr_project.se.storage.common.dto.FriendDto;

import java.util.List;
import java.util.Optional;

@Component
public class UpdateSubscriptionFriendHandlerImpl implements MetaRequestHandler {

    private final ClientStorage clientStorage;

    public UpdateSubscriptionFriendHandlerImpl(ClientStorage clientStorage) {
        this.clientStorage = clientStorage;
    }

    @Override
    @Transactional
    public Optional<String> handle(MetaRequestDto requestDto) {
        Optional<FriendDto> friendDtoO = requestDto.friendDto();
        if (friendDtoO.isEmpty()) {
            return Optional.empty();
        }
        FriendDto friendDto = friendDtoO.get();


        Optional<ClientDto> dto = clientStorage.findByChatId(requestDto.chatId());
        if (dto.isEmpty()) {
            return Optional.empty();
        }

        ClientDto clientDto = dto.get();
        List<FriendDto> friendDtos = clientDto.getListOfFriends();

        for (FriendDto f : friendDtos) {
            if (f.getTelegramId().equals(friendDto.getTelegramId()) || f.getChatId().equals(friendDto.getChatId())) {
                updating(f, friendDto);
            }
        }
        clientStorage.save(clientDto);

        return Optional.empty();
    }

    private void updating(FriendDto fromDb, FriendDto toDb) {
        Optional.ofNullable(toDb.getEmail()).ifPresent(fromDb::setEmail);
        Optional.ofNullable(toDb.getTelegramId()).ifPresent(fromDb::setTelegramId);
        Optional.ofNullable(toDb.getChatId()).ifPresent(fromDb::setChatId);
        Optional.ofNullable(toDb.getPhoneNumber()).ifPresent(fromDb::setPhoneNumber);
        Optional.ofNullable(toDb.getWayToNotify()).ifPresent(fromDb::setWayToNotify);
    }

    @Override
    public MessageType getMessageType() {
        return new MessageType(EntityType.SUBSCRIPTION, FunctionType.UPDATE);
    }
}
