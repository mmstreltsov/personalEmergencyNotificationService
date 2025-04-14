package ru.hse.mmstr_project.se.storage.fast_storage.dto;

import java.io.Serializable;

public record IncidentMetadataDto(
        Long id,
//        String text,
//        Long clientId,
        Long firstTimeToActivate
//        List<Long> listTimesToActivate,
//        Integer allowedDelayAfterPing,
//        Boolean okFromAntispam,
//        String textToPing,
//        Long telegramId,
//        Long chatId,
//        List<FriendMetaDto> listOfFriends
) implements Serializable {
}
