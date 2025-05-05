package ru.hse.mmstr_project.se.kafka.dto;

import ru.hse.mmstr_project.se.storage.fast_storage.dto.IncidentMetadataDto;

import java.util.List;

public record PingerDto(
        String name,
        String text,
        String textToPing,
        Long chatId,
        List<FriendDto> friends)
{
    public static PingerDto cons(IncidentMetadataDto dto) {
        return new PingerDto(
                dto.name(),
                dto.text(),
                dto.textToPing(),
                dto.chatId(),
                dto.listOfFriends().stream().map(it -> new FriendDto(it.name(), it.wayToNotify())).toList());
    }

    public record FriendDto(
            String name,
            List<String> wayToNotify)
    {
    }
}
