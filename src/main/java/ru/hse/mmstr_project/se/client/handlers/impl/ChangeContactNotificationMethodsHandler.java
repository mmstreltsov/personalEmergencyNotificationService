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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ChangeContactNotificationMethodsHandler implements CommandHandler {

    private final MetaRequestService metaRequestService;

    public ChangeContactNotificationMethodsHandler(MetaRequestService metaRequestService) {
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

        List<String> wayToNotify = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Optional<String> wayO = getArg(args, i);
            if (wayO.isEmpty()) {
                break;
            }

            String way = wayO.get();

            if (SenderWayToNotifyUtil.isCorrect(way)) {
                wayToNotify.add(way);
            } else {
                return Optional.of("Предоставлен неверный способ уведомления {" + way + "}. Вам нужно что-то из: " + SenderWayToNotifyUtil.correctWays);
            }
        }

        friendDto.setWayToNotify(wayToNotify);

        metaRequestService.sendMessage(new MetaRequestDto(
                FunctionType.UPDATE,
                EntityType.CLIENT_FRIEND,
                chatId,
                Optional.empty(),
                Optional.of(friendDto),
                List.of(),
                false));
        return Optional.empty();
    }

    @Override
    public String getCommand() {
        return "/set_notification_methods";
    }
}
