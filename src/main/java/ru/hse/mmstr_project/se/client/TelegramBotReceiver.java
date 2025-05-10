package ru.hse.mmstr_project.se.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hse.mmstr_project.se.client.handlers.CommandHandler;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Component
public class TelegramBotReceiver extends TelegramLongPollingBot {

    private static final String SPACE = " ";
    private static final String ERROR_CASE = "Ваша команда не распознана";

    private final String botUsername;
    private final Map<String, CommandHandler> handlers;
    private final TelegramBotSender telegramBotSender;

    public TelegramBotReceiver(
            @Value("${tg.bot.main.client.token}") String botToken,
            @Value("${tg.bot.main.client.username}") String botUsername,
            Map<String, CommandHandler> commandHandlers,
            TelegramBotSender telegramBotSender) {
        super(botToken);
        this.botUsername = botUsername;
        this.handlers = commandHandlers;
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

        String messageText = update.getMessage().getText().trim();
        Long chatId = update.getMessage().getChatId();

        String[] input = messageText.split(SPACE);
        (input.length > 0 ? Optional.of(input[0].toLowerCase()) : Optional.<String>empty())
                .flatMap(it -> Optional.ofNullable(handlers.get(it)))
                .map(cons -> {
                    String args = String.join(SPACE, Arrays.stream(input).skip(1).toList()).trim();
                    return cons.handle(args, chatId);
                })
                .orElse(Optional.of(ERROR_CASE))
                .ifPresent(response -> telegramBotSender.sendMessage(chatId, response));
    }
}