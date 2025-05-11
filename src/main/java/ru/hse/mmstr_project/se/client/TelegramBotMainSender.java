package ru.hse.mmstr_project.se.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class TelegramBotMainSender extends DefaultAbsSender {

    public TelegramBotMainSender(
            @Value("${tg.bot.main.client.token}") String botToken) {
        super(new DefaultBotOptions(), botToken);
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(forMarkdown(text));
        message.setParseMode("MarkDown");

        try {
            execute(message);
        } catch (TelegramApiException ignored) {
        }
    }

    private String forMarkdown(String text) {
        List<String> specialChars = List.of("_", "*", "[");

        for (String character : specialChars) {
            text = text.replace(character, "\\" + character);
        }
        return text;
    }
}