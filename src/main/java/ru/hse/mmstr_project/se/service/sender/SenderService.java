package ru.hse.mmstr_project.se.service.sender;

import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.kafka.dto.SenderRequestDto;
import ru.hse.mmstr_project.se.service.kafka.producer.SenderProducer;
import ru.hse.mmstr_project.se.storage.fast_storage.dto.IncidentMetadataDto;

import java.util.HashSet;
import java.util.List;

@Component
public class SenderService {

    private final SenderProducer senderProducer;

    public SenderService(SenderProducer senderProducer) {
        this.senderProducer = senderProducer;
    }

    public void send(List<IncidentMetadataDto> requests, boolean useTextWrapper) {
        requests.stream()
                .flatMap(request -> request.listOfFriends().stream()
                        .filter(friend -> new HashSet<>(request.friendIds()).contains((long) friend.id()))
                        .map(friend -> new SenderRequestDto(
                                request.text(),
                                useTextWrapper,
                                null,
                                request.username(),
                                request.telegramId(),
                                friend.wayToNotify(),
                                friend.phoneNumber(),
                                friend.chatId(),
                                friend.email())))
                .forEach(this::sendOne);
    }

    public void sendOne(SenderRequestDto send) {
        if (SenderWayToNotifyUtil.isTgSender(send.wayToNotify())) {
            senderProducer.sendMessageToTg(send);
        }
        if (SenderWayToNotifyUtil.isEmailSender(send.wayToNotify())) {
            senderProducer.sendMessageToEmail(send);
        }
        if (SenderWayToNotifyUtil.isSmsSender(send.wayToNotify())) {
            senderProducer.sendMessageToSms(send);
        }
    }
}
