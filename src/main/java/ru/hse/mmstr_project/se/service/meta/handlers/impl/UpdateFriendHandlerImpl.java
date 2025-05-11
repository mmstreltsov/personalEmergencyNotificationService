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
import java.util.Objects;
import java.util.Optional;

@Component
public class UpdateFriendHandlerImpl implements MetaRequestHandler {

    private final ClientStorage clientStorage;

    public UpdateFriendHandlerImpl(ClientStorage clientStorage) {
        this.clientStorage = clientStorage;
    }

    @Override
    public Optional<String> handle(MetaRequestDto requestDto) {
        Optional<FriendDto> friendDtoO = requestDto.friendDto();
        if (friendDtoO.isEmpty()) {
            return Optional.of("Ничего не делается");
        }
        FriendDto friendDto = friendDtoO.get();


        Optional<ClientDto> dto = clientStorage.findByChatId(requestDto.chatId());
        if (dto.isEmpty()) {
            return Optional.of("Ваш аккаунт не найден, попробуйте /start");
        }

        ClientDto clientDto = dto.get();
        List<FriendDto> friendDtos = new ArrayList<>(clientDto.getListOfFriends());

        Optional<FriendDto> dtoFromDb = friendDtos.stream().filter(it -> it.getId().equals(friendDto.getId())).findAny();
        if (dtoFromDb.isEmpty()) {
            return Optional.of("Контакт с таким айди не существует");
        }

        updating(dtoFromDb.get(), friendDto);
        clientStorage.save(clientDto);

        if (Objects.nonNull(friendDto.getTelegramId())) {
            return Optional.of(String.format("""
                    Внимание, ваш контакт может получить сообщение в телеграм только после подписки на вас через бота.
                    Перешлите ему это сообщение:
                    
                    `Зайдите в телеграм-бота @EmergencyNotificationsSender_bot и выполните /start и `/subscribe %s``
                    """, requestDto.chatId().toString()));
        }

        return Optional.empty();
    }

    private void updating(FriendDto fromDb, FriendDto toDb) {
        Optional.ofNullable(toDb.getName()).ifPresent(fromDb::setName);
        Optional.ofNullable(toDb.getEmail()).ifPresent(fromDb::setEmail);
        Optional.ofNullable(toDb.getTelegramId()).ifPresent(fromDb::setTelegramId);
        Optional.ofNullable(toDb.getPhoneNumber()).ifPresent(fromDb::setPhoneNumber);
        Optional.ofNullable(toDb.getWayToNotify()).ifPresent(fromDb::setWayToNotify);
    }

    @Override
    public MessageType getMessageType() {
        return new MessageType(EntityType.CLIENT_FRIEND, FunctionType.UPDATE);
    }
}
