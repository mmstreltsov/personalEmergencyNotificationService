package ru.hse.mmstr_project.se.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hse.mmstr_project.se.client.cache.ClientCacheService;

@Component
public class TelegramBotReceiver extends TelegramLongPollingBot {

    private final ClientCacheService clientCacheService;
    private final String botUsername;

    public TelegramBotReceiver(
            @Value("${tg.bot.main.client.token}") String botToken,
            @Value("${tg.bot.main.client.username}") String botUsername,
            ClientCacheService clientCacheService) {
        super(botToken);
        this.botUsername = botUsername;
        this.clientCacheService = clientCacheService;
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

        String messageText = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
    }
}