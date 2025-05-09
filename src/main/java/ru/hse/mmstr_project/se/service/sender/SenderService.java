package ru.hse.mmstr_project.se.service.sender;

import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.kafka.dto.SenderRequestDto;
import ru.hse.mmstr_project.se.service.kafka.producer.SenderProducer;
import ru.hse.mmstr_project.se.storage.fast_storage.dto.IncidentMetadataDto;

import java.util.List;

@Component
public class SenderService {

    private final SenderProducer senderProducer;

    public SenderService(SenderProducer senderProducer) {
        this.senderProducer = senderProducer;
    }

    public void send(List<IncidentMetadataDto> requests) {
        requests.stream()
                .flatMap(request -> request.listOfFriends().stream().map(friend -> new SenderRequestDto(
                        request.text(),
                        null,
                        request.username(),
                        request.telegramId(),
                        friend.wayToNotify(),
                        friend.phoneNumber(),
                        friend.chatId(),
                        friend.email())))
                .forEach(this::sendOne);
    }

    private void sendOne(SenderRequestDto send) {
        if (isTgSender(send.wayToNotify())) {
            senderProducer.sendMessageToTg(send);
        }
        if (isEmailSender(send.wayToNotify())) {
            senderProducer.sendMessageToEmail(send);
        }
        if (isSmsSender(send.wayToNotify())) {
            senderProducer.sendMessageToSms(send);
        }
    }

    private boolean isTgSender(List<String> wayToNotify) {
        return isSenderImpl(wayToNotify, "tg");
    }

    private boolean isEmailSender(List<String> wayToNotify) {
        return isSenderImpl(wayToNotify, "email");
    }

    private boolean isSmsSender(List<String> wayToNotify) {
        return isSenderImpl(wayToNotify, "sms");
    }

    private boolean isSenderImpl(List<String> wayToNotify, String text) {
        return wayToNotify.stream().anyMatch(way -> way.contains(text));
    }
}
