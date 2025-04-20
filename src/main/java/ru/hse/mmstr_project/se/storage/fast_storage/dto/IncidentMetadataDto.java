package ru.hse.mmstr_project.se.storage.fast_storage.dto;

import ru.hse.mmstr_project.se.storage.common.dto.ClientDto;
import ru.hse.mmstr_project.se.storage.common.dto.ScenarioDto;

import java.time.Instant;
import java.util.List;

public record IncidentMetadataDto(
        Long id,
        String text,
        Long firstTimeToActivate,
        List<Long> listTimesToActivate,
        Integer allowedDelayAfterPing,
        String textToPing,
        String telegramId,
        Long chatId,
        List<FriendMetaDto> listOfFriends
) {

    public static IncidentMetadataDto parse(ScenarioDto scenarioDto, ClientDto clientDto) {
        return new IncidentMetadataDto(
                scenarioDto.getId(),
                scenarioDto.getText(),
                scenarioDto.getFirstTimeToActivate().toEpochMilli(),
                scenarioDto.getListTimesToActivate().stream().map(Instant::toEpochMilli).toList(),
                scenarioDto.getAllowedDelayAfterPing(),
                scenarioDto.getTextToPing(),
                clientDto.getTelegramId(),
                clientDto.getChatId(),
                clientDto.getListOfFriends().stream().map(FriendMetaDto::parse).toList());
    }
}
