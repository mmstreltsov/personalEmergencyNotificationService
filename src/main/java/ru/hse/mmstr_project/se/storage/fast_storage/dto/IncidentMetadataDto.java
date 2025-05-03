package ru.hse.mmstr_project.se.storage.fast_storage.dto;

import ru.hse.mmstr_project.se.storage.common.dto.ClientDto;
import ru.hse.mmstr_project.se.storage.common.dto.ScenarioDto;

import java.util.List;

public final class IncidentMetadataDto {
    private final String id;
    private final String name;
    private final String text;
    private final Long firstTimeToActivate;
    private final Long firstTimeToActivateOrigin;
    private final Integer allowedDelayAfterPing;
    private final String textToPing;
    private final String telegramId;
    private final Long chatId;
    private final List<FriendMetaDto> listOfFriends;

    public IncidentMetadataDto(
            String id,
            String name,
            String text,
            Long firstTimeToActivate,
            Long firstTimeToActivateOrigin,
            Integer allowedDelayAfterPing,
            String textToPing,
            String telegramId,
            Long chatId,
            List<FriendMetaDto> listOfFriends
    ) {
        this.id = id;
        this.name = name;
        this.text = text;
        this.firstTimeToActivate = firstTimeToActivate;
        this.firstTimeToActivateOrigin = firstTimeToActivateOrigin;
        this.allowedDelayAfterPing = allowedDelayAfterPing;
        this.textToPing = textToPing;
        this.telegramId = telegramId;
        this.chatId = chatId;
        this.listOfFriends = listOfFriends;
    }

    public static IncidentMetadataDto parse(ScenarioDto scenarioDto, ClientDto clientDto) {
        return new IncidentMetadataDto(
                scenarioDto.getUuid().toString(),
                scenarioDto.getName(),
                scenarioDto.getText(),
                scenarioDto.getFirstTimeToActivate().toEpochMilli(),
                scenarioDto.getFirstTimeToActivateOrigin().toEpochMilli(),
                scenarioDto.getAllowedDelayAfterPing(),
                scenarioDto.getTextToPing(),
                clientDto.getTelegramId(),
                clientDto.getChatId(),
                clientDto.getListOfFriends().stream().map(FriendMetaDto::parse).toList());
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String text() {
        return text;
    }

    public Long firstTimeToActivate() {
        return firstTimeToActivate;
    }

    public Long firstTimeToActivateOrigin() {
        return firstTimeToActivateOrigin;
    }

    public Integer allowedDelayAfterPing() {
        return allowedDelayAfterPing;
    }

    public String textToPing() {
        return textToPing;
    }

    public String telegramId() {
        return telegramId;
    }

    public Long chatId() {
        return chatId;
    }

    public List<FriendMetaDto> listOfFriends() {
        return listOfFriends;
    }
}
