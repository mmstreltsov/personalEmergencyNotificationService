package ru.hse.mmstr_project.se.storage.fast_storage.dto;

import ru.hse.mmstr_project.se.storage.common.dto.ClientDto;
import ru.hse.mmstr_project.se.storage.common.dto.ScenarioDto;

import java.util.List;

public record IncidentMetadataDto(
        String id,
        String name,
        String text,
        Long firstTimeToActivate,
        Long firstTimeToActivateOrigin,
        Integer allowedDelayAfterPing,
        String textToPing,
        String telegramId,
        String username,
        Long chatId,
        List<FriendMetaDto> listOfFriends,
        List<Long> friendIds
) {

    public static IncidentMetadataDto parse(ScenarioDto scenarioDto, ClientDto clientDto) {
        return new IncidentMetadataDto(
                scenarioDto.getUuid().toString(),
                scenarioDto.getName(),
                scenarioDto.getText(),
                scenarioDto.getFirstTimeToActivate().toEpochMilli() + scenarioDto.getAllowedDelayAfterPing() * 60_000,
                scenarioDto.getFirstTimeToActivateOrigin().toEpochMilli(),
                scenarioDto.getAllowedDelayAfterPing(),
                scenarioDto.getTextToPing(),
                clientDto.getTelegramId(),
                clientDto.getName(),
                clientDto.getChatId(),
                clientDto.getListOfFriends().stream().map(FriendMetaDto::parse).toList(),
                scenarioDto.getFriendsIds());
    }
}
