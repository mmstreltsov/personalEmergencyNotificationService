package ru.hse.mmstr_project.se.client.handlers.impl;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hse.mmstr_project.se.client.handlers.CommandHandler;

import java.util.Optional;

@Component
public class HelpHandler implements CommandHandler {

    private static final String HELP_TEXT = """
            Привет
            """;

    @Override
    public Optional<String> handle(String args, Long chatId, Message message) {
        return Optional.of(HELP_TEXT);
    }

    @Override
    public String getCommand() {
        return "/help";
    }
}
