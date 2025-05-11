package ru.hse.mmstr_project.se.storage.common.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class CreateScenarioDto {
    private UUID uuid;
    private String name;
    private String text;
    private Long clientId;
    private List<Long> friendsIds;
    private Instant firstTimeToActivate;
    private Instant firstTimeToActivateOrigin;
    private Integer allowedDelayAfterPing;
    private Boolean okFromAntispam;
    private Boolean okByHand;
    private String textToPing;

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

    public CreateScenarioDto(ScenarioDto scenarioDto) {
        this(
                scenarioDto.getUuid(),
                scenarioDto.getName(),
                scenarioDto.getText(),
                scenarioDto.getClientId(),
                scenarioDto.getFriendsIds(),
                scenarioDto.getFirstTimeToActivate(),
                scenarioDto.getFirstTimeToActivateOrigin(),
                scenarioDto.getAllowedDelayAfterPing(),
                scenarioDto.getOkFromAntispam(),
                scenarioDto.getOkByHand(),
                scenarioDto.getTextToPing());
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

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public void setFriendsIds(List<Long> friendsIds) {
        this.friendsIds = friendsIds;
    }

    public void setFirstTimeToActivate(Instant firstTimeToActivate) {
        this.firstTimeToActivate = firstTimeToActivate;
    }

    public void setFirstTimeToActivateOrigin(Instant firstTimeToActivateOrigin) {
        this.firstTimeToActivateOrigin = firstTimeToActivateOrigin;
    }

    public void setAllowedDelayAfterPing(Integer allowedDelayAfterPing) {
        this.allowedDelayAfterPing = allowedDelayAfterPing;
    }

    public void setOkFromAntispam(Boolean okFromAntispam) {
        this.okFromAntispam = okFromAntispam;
    }

    public void setOkByHand(Boolean okByHand) {
        this.okByHand = okByHand;
    }

    public void setTextToPing(String textToPing) {
        this.textToPing = textToPing;
    }
}
