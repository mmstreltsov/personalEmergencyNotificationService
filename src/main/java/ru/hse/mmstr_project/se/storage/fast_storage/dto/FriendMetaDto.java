package ru.hse.mmstr_project.se.storage.fast_storage.dto;

import ru.hse.mmstr_project.se.storage.common.dto.FriendDto;

import java.util.List;

public record FriendMetaDto(
        Integer id,
        String name,
        List<String> wayToNotify,
        String phoneNumber,
        Integer telegramId,
        Integer chatId,
        String email) {

    public static FriendMetaDto parse(FriendDto friendDto) {
        return new FriendMetaDto(
                friendDto.getId(),
                friendDto.getName(),
                friendDto.getWayToNotify(),
                friendDto.getPhoneNumber(),
                friendDto.getTelegramId(),
                friendDto.getChatId(),
                friendDto.getEmail());
    }
}
