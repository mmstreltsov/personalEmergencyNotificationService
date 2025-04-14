package ru.hse.mmstr_project.se.storage.fast_storage.dto;

import java.util.List;

public record FriendMetaDto(
        Integer id,
        String name,
        List<String> wayToNotify,
        String phoneNumber,
        Integer telegramId,
        Integer chatId,
        String email) {
}
