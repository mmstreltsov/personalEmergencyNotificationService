package ru.hse.mmstr_project.se.client.handlers.impl;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hse.mmstr_project.se.client.handlers.CommandHandler;
import ru.hse.mmstr_project.se.kafka.dto.MetaRequestDto;
import ru.hse.mmstr_project.se.service.kafka.producer.MetaRequestService;
import ru.hse.mmstr_project.se.service.meta.EntityType;
import ru.hse.mmstr_project.se.service.meta.FunctionType;
import ru.hse.mmstr_project.se.service.sender.SenderWayToNotifyUtil;
import ru.hse.mmstr_project.se.storage.common.dto.FriendDto;

import java.util.Optional;

@Component
public class ChangeContactNotificationDataHandler implements CommandHandler {

    private final MetaRequestService metaRequestService;

    public ChangeContactNotificationDataHandler(MetaRequestService metaRequestService) {
        this.metaRequestService = metaRequestService;
    }

    @Override
    public Optional<String> handle(String args, Long chatId, Message message) {
        Optional<String> id = getArg(args, 0);
        if (id.isEmpty()) {
            return Optional.of("Предоставьте айди контакта");
        }

        FriendDto friendDto = new FriendDto();
        friendDto.setId(Integer.parseInt(id.get().trim()));

        Optional<String> wayO = getArg(args, 1).filter(SenderWayToNotifyUtil::isCorrect);
        if (wayO.isEmpty()) {
            return Optional.of("Предоставьте способ уведомления");
        }

        String data = getArg(args, 2).orElse("");
        String way = wayO.get();

        if (SenderWayToNotifyUtil.isTgSender(way)) {
            friendDto.setTelegramId(data);
        } else if (SenderWayToNotifyUtil.isEmailSender(way)) {
            friendDto.setEmail(data);
        } else if (SenderWayToNotifyUtil.isSmsSender(way)) {
            friendDto.setPhoneNumber(data);
        }

        metaRequestService.sendMessage(new MetaRequestDto(
                FunctionType.UPDATE,
                EntityType.CLIENT_FRIEND,
                chatId,
                Optional.empty(),
                Optional.of(friendDto),
                Optional.empty(),
                false));
        return Optional.empty();
    }

    @Override
    public String getCommand() {
        return "/set_contact_data";
    }
}
