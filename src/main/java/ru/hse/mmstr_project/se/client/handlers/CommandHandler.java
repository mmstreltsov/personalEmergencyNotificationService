package ru.hse.mmstr_project.se.client.handlers;


import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Optional;

public interface CommandHandler {
    Optional<String> handle(String args, Long chatId, Message message);

    String getCommand();

    default Optional<String> getArg(String args, int index) {
        String[] s = args.trim().split(" ");
        return args.isEmpty() || s.length <= index ? Optional.empty() : Optional.of(s[index].trim());
    }
}
