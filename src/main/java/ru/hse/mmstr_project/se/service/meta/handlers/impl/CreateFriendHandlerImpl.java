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
public class CreateFriendHandlerImpl implements MetaRequestHandler {

    private final ClientStorage clientStorage;

    public CreateFriendHandlerImpl(ClientStorage clientStorage) {
        this.clientStorage = clientStorage;
    }

    @Override
    public Optional<String> handle(MetaRequestDto requestDto) {
        Optional<ClientDto> dto = clientStorage.findByChatId(requestDto.chatId());
        if (dto.isEmpty()) {
            return Optional.of("Ваш аккаунт не найден, попробуйте /start");
        }

        ClientDto clientDto = dto.get();
        List<FriendDto> friendDtos = new ArrayList<>(clientDto.getListOfFriends());

        if (!(validateFriendsCount(friendDtos))) {
            StringBuilder response = new StringBuilder("Превышено разрешенное количество добавляемых контактов, попробуйте удалить неактуальных:\n\n");
            for (FriendDto f : friendDtos) {
                response.append("id: ").append(f.getId())
                        .append(", name:").append(f.getName()).append("\n");
            }

            return Optional.of(response.toString());
        }

        int id = friendDtos.size() + 1;
        friendDtos.add(new FriendDto(id, "", List.of(), "", "", null, ""));
        clientDto.setListOfFriends(friendDtos);

        clientStorage.save(clientDto);

        return Optional.of("Создан контакт с id: " + id);
    }


    private boolean validateFriendsCount(List<FriendDto> friendDtos) {
        return friendDtos.size() < 10;
    }

    @Override
    public MessageType getMessageType() {
        return new MessageType(EntityType.CLIENT_FRIEND, FunctionType.CREATE);
    }
}
