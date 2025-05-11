package ru.hse.mmstr_project.se.client.senderBot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class TelegramBotSenderReceiver extends TelegramLongPollingBot {

    private final String botUsername;
    private final TelegramBotSender telegramBotSender;

    public TelegramBotSenderReceiver(
            @Value("${tg.bot.sender.client.token}") String botToken,
            @Value("${tg.bot.sender.client.username}") String botUsername,
            TelegramBotSender telegramBotSender) {
        super(botToken);
        this.botUsername = botUsername;
        this.telegramBotSender = telegramBotSender;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }


    }
}