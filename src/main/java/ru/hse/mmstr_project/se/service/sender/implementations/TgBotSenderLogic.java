package ru.hse.mmstr_project.se.service.sender.implementations;

import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.client.senderBot.TelegramBotSender;
import ru.hse.mmstr_project.se.kafka.dto.SenderRequestDto;

import java.util.Optional;

@Component
public class TgBotSenderLogic implements CommonSenderLogic {

    private final TelegramBotSender sender;

    public TgBotSenderLogic(TelegramBotSender sender) {
        this.sender = sender;
    }

    @Override
    public boolean sendMessage(SenderRequestDto message) {
        Optional.ofNullable(message.chatId())
                .ifPresent(it -> sender.sendMessage(it, SenderTextProcessUtil.prompt(message), message.data()));
        return true;
    }
}
