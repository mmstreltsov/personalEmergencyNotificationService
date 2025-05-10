package ru.hse.mmstr_project.se.client.handlers.impl;

import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.client.handlers.CommandHandler;

import java.util.Optional;

@Component
public class HelpHandler implements CommandHandler {

    private static final String HELP_TEXT = """
            Привет
            """;

    @Override
    public Optional<String> handle(String args, Long chatId) {
        return Optional.of(HELP_TEXT);
    }
}
