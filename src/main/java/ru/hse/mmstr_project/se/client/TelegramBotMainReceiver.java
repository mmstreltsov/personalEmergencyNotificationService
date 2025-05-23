package ru.hse.mmstr_project.se.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hse.mmstr_project.se.client.handlers.CommandHandler;
import ru.hse.mmstr_project.se.client.handlers.impl.SosButtonHandler;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
public class TelegramBotMainReceiver extends TelegramLongPollingBot {

    private static final String SPACE = " ";
    private static final String ERROR_CASE = "Ваша команда не распознана";
    private static final String RUNTIME_ERROR_CASE = "Ошибка при выполнении команды";

    private final String botUsername;
    private final Map<String, CommandHandler> handlers;
    private final SosButtonHandler sosButtonHandler;
    private final TelegramBotMainSender telegramBotMainSender;

    public TelegramBotMainReceiver(
            @Value("${tg.bot.main.client.token}") String botToken,
            @Value("${tg.bot.main.client.username}") String botUsername,
            Map<String, CommandHandler> commandHandlers,
            SosButtonHandler sosButtonHandler,
            TelegramBotMainSender telegramBotMainSender) {
        super(botToken);
        this.botUsername = botUsername;
        this.handlers = commandHandlers;
        this.sosButtonHandler = sosButtonHandler;
        this.telegramBotMainSender = telegramBotMainSender;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) {
            return;
        }
        if (update.getMessage().hasLocation()) {
            sosButtonHandler.handleWithLocation(update.getMessage().getChatId(), update.getMessage().getLocation());
            return;
        }
        if (!update.getMessage().hasText()) {
            return;
        }

        String messageText = update.getMessage().getText().trim();
        Long chatId = update.getMessage().getChatId();
        if (Objects.isNull(chatId)) {
            return;
        }

        String[] input = messageText.split(SPACE);
        try {
            (input.length > 0 ? Optional.of(input[0].toLowerCase()) : Optional.<String>empty())
                    .flatMap(it -> Optional.ofNullable(handlers.get(it)))
                    .map(cons -> {
                        String args = String.join(SPACE, Arrays.stream(input).skip(1).toList()).trim();
                        return cons.handle(args, chatId, update.getMessage());
                    })
                    .orElse(Optional.of(ERROR_CASE))
                    .ifPresent(response -> telegramBotMainSender.sendMessage(chatId, response));
        } catch (Exception e) {
            telegramBotMainSender.sendMessage(chatId, RUNTIME_ERROR_CASE);
        }
    }
}