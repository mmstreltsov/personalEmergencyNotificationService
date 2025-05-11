package ru.hse.mmstr_project.se.kafka.dto;

import java.util.List;

public record SenderRequestDto(
        String text,
        byte[] data,
        String username,
        String telegramId,
        List<String> wayToNotify,
        String phoneNumber,
        Long chatId,
        String email) {
}
