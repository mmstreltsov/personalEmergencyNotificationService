package ru.hse.mmstr_project.se.storage.common.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class CreateScenarioDto {
    private final UUID uuid;
    private final String name;
    private final String text;
    private final Long clientId;
    private final List<Long> friendsIds;
    private final Instant firstTimeToActivate;
    private final Instant firstTimeToActivateOrigin;
    private final Integer allowedDelayAfterPing;
    private final Boolean okFromAntispam;
    private final Boolean okByHand;
    private final String textToPing;

    public CreateScenarioDto(
            UUID uuid,
            String name,
            String text,
            Long clientId,
            List<Long> friendsIds,
            Instant firstTimeToActivate,
            Instant firstTimeToActivateOrigin,
            Integer allowedDelayAfterPing,
            Boolean okFromAntispam,
            Boolean okByHand,
            String textToPing) {
        this.uuid = uuid;
        this.name = name;
        this.text = text;
        this.clientId = clientId;
        this.friendsIds = friendsIds;
        this.firstTimeToActivate = firstTimeToActivate;
        this.firstTimeToActivateOrigin = firstTimeToActivateOrigin;
        this.allowedDelayAfterPing = allowedDelayAfterPing;
        this.okFromAntispam = okFromAntispam;
        this.okByHand = okByHand;
        this.textToPing = textToPing;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public Long getClientId() {
        return clientId;
    }

    public List<Long> getFriendsIds() {
        return friendsIds;
    }

    public Instant getFirstTimeToActivate() {
        return firstTimeToActivate;
    }

    public Instant getFirstTimeToActivateOrigin() {
        return firstTimeToActivateOrigin;
    }

    public Integer getAllowedDelayAfterPing() {
        return allowedDelayAfterPing;
    }

    public Boolean getOkFromAntispam() {
        return okFromAntispam;
    }

    public Boolean getOkByHand() {
        return okByHand;
    }

    public String getTextToPing() {
        return textToPing;
    }
}
