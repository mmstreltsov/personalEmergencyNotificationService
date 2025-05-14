package ru.hse.mmstr_project.se.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
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
            throw new RuntimeException();
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

    public void requestLocationForSosButton(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Вы можете поделиться своим местоположением для передачи информации своим контактам.");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        KeyboardButton locationButton = new KeyboardButton("📍 Отправить местоположение");
        locationButton.setRequestLocation(true);
        row.add(locationButton);

        row.add(new KeyboardButton("/no"));

        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException ignored) {
        }
    }
}