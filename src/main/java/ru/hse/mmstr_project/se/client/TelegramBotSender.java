package ru.hse.mmstr_project.se.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBotSender extends DefaultAbsSender {

    public TelegramBotSender(
            @Value("${tg.bot.main.client.token}") String botToken) {
        super(new DefaultBotOptions(), botToken);
    }

    public void sendMessage(Long chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        execute(message);
    }
}