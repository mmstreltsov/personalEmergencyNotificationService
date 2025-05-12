package ru.hse.mmstr_project.se.service.sender.implementations;

import ru.hse.mmstr_project.se.kafka.dto.SenderRequestDto;

public class SenderTextProcessUtil {

    public static String prompt(SenderRequestDto requestDto) {
        if (!requestDto.useTextWrapper()) {
            return requestDto.text();
        }
        return prompt(requestDto.text(), requestDto.username(), requestDto.telegramId());
    }

    public static String prompt(String text, String username, String telegramId) {
        return String.format(
                "Привет, Ваш друг %s (telegramId %s) передает: %s. Свяжитесь с ним",
                username,
                telegramId,
                text);
    }
}
