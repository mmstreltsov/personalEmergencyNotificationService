package ru.hse.mmstr_project.se.client.senderBot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hse.mmstr_project.se.kafka.dto.MetaRequestDto;
import ru.hse.mmstr_project.se.service.kafka.producer.MetaRequestService;
import ru.hse.mmstr_project.se.service.meta.EntityType;
import ru.hse.mmstr_project.se.service.meta.FunctionType;
import ru.hse.mmstr_project.se.storage.common.dto.FriendDto;

import java.util.List;
import java.util.Optional;

@Component
public class TelegramBotSubscriptionsReceiver extends TelegramLongPollingBot {

    private static final String HELP_TEXT = """
            Бот для определения подписки на состояние пользователей.
                        
            /subscribe {id} -- подписаться на пользователя
            /unsubscribe {id} -- отписаться от пользователя пользователя
            /help -- вывести это сообщение
            """;

    private final String botUsername;
    private final TelegramBotSender telegramBotSender;
    private final MetaRequestService metaRequestService;

    public TelegramBotSubscriptionsReceiver(
            @Value("${tg.bot.sender.client.token}") String botToken,
            @Value("${tg.bot.sender.client.username}") String botUsername,
            TelegramBotSender telegramBotSender,
            MetaRequestService metaRequestService) {
        super(botToken);
        this.botUsername = botUsername;
        this.telegramBotSender = telegramBotSender;
        this.metaRequestService = metaRequestService;
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

        Long chatId = update.getMessage().getChatId();
        String[] args = update.getMessage().getText().trim().split(" ");

        if (args.length == 1 && args[0].equalsIgnoreCase("/start")) {
            return;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("/help")) {
            callback(HELP_TEXT, chatId);
            return;
        }

        if (args.length != 2) {
            callback("Не могу распарсить ввод", chatId);
            return;
        }

        long mainUserChatId;
        try {
            mainUserChatId = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            callback("Не могу распарсить ввод, введено не число", chatId);
            return;
        }

        FriendDto friendDto = new FriendDto();
        friendDto.setTelegramId(update.getMessage().getFrom().getUserName());

        MetaRequestDto metaRequestDto = new MetaRequestDto(
                FunctionType.UPDATE,
                EntityType.SUBSCRIPTION,
                mainUserChatId,
                Optional.empty(),
                Optional.of(friendDto),
                List.of());

        switch (args[0].toLowerCase()) {
            case "/subscribe":
                friendDto.setChatId(chatId);
                break;
            case "/unsubscribe":
                friendDto.setChatId(0L);
                break;
            default:
                callback("Неизвестная команда", chatId);
                return;
        }
        metaRequestService.sendMessage(metaRequestDto);
    }

    private void callback(String response, long chatId) {
        telegramBotSender.sendMessage(chatId, response);
    }
}