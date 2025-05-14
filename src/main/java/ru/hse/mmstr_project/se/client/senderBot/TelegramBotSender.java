package ru.hse.mmstr_project.se.client.senderBot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.util.List;

@Component
public class TelegramBotSender extends DefaultAbsSender {

    public TelegramBotSender(
            @Value("${tg.bot.sender.client.token}") String botToken) {
        super(new DefaultBotOptions(), botToken);
    }

    public void sendMessage(Long chatId, String text, byte[] photo) {
        if (photo == null || photo.length == 0) {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(forMarkdown(text));
            message.setParseMode("MarkDown");

            try {
                execute(message);
            } catch (TelegramApiException ignored) {
            }
        } else {
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId);
            sendPhoto.setPhoto(new InputFile(new ByteArrayInputStream(photo), "coordinates.jpg"));
            if (text != null && !text.isEmpty()) {
                sendPhoto.setCaption(text);
                sendPhoto.setParseMode("MarkDown");
            }

            try {
                execute(sendPhoto);
            } catch (TelegramApiException ignored) {
            }
        }
    }

    private String forMarkdown(String text) {
        List<String> specialChars = List.of("_", "*", "[");

        for (String character : specialChars) {
            text = text.replace(character, "\\" + character);
        }
        text = text.replace("UNDERLINING", "_");
        return text;
    }
}