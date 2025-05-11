package ru.hse.mmstr_project.se.client.handlers.impl;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hse.mmstr_project.se.client.handlers.CommandHandler;

import java.util.Optional;

@Component
public class NoHandler implements CommandHandler {

    @Override
    public Optional<String> handle(String args, Long chatId, Message message) {
        return Optional.empty();
    }

    @Override
    public String getCommand() {
        return "/no";
    }
}
