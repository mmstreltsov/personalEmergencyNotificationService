package ru.hse.mmstr_project.se.client.handlers;

import java.util.Optional;

public interface CommandHandler {
    Optional<String> handle(String args, Long chatId);
}
