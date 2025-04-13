package ru.hse.mmstr_project.se.storage.common.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CreateScenarioDto {

    private String text;
    private Long clientId;
    private List<Long> friendIds;
    private LocalDateTime firstTimeToActivate;
    private List<LocalDateTime> listTimesToActivate;
    private Integer allowedDelayAfterPing;
    private Boolean okFromAntispam;
    private String textToPing;

    public CreateScenarioDto(
            String text,
            Long clientId,
            List<Long> friendIds,
            LocalDateTime firstTimeToActivate,
            List<LocalDateTime> listTimesToActivate,
            Integer allowedDelayAfterPing,
            Boolean okFromAntispam,
            String textToPing) {
        this.text = text;
        this.clientId = clientId;
        this.friendIds = friendIds;
        this.firstTimeToActivate = firstTimeToActivate;
        this.listTimesToActivate = listTimesToActivate;
        this.allowedDelayAfterPing = allowedDelayAfterPing;
        this.okFromAntispam = okFromAntispam;
        this.textToPing = textToPing;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public List<Long> getFriendIds() {
        return friendIds;
    }

    public void setFriendIds(List<Long> friendIds) {
        this.friendIds = friendIds;
    }

    public LocalDateTime getFirstTimeToActivate() {
        return firstTimeToActivate;
    }

    public void setFirstTimeToActivate(LocalDateTime firstTimeToActivate) {
        this.firstTimeToActivate = firstTimeToActivate;
    }

    public List<LocalDateTime> getListTimesToActivate() {
        return listTimesToActivate;
    }

    public void setListTimesToActivate(List<LocalDateTime> listTimesToActivate) {
        this.listTimesToActivate = listTimesToActivate;
    }

    public Integer getAllowedDelayAfterPing() {
        return allowedDelayAfterPing;
    }

    public void setAllowedDelayAfterPing(Integer allowedDelayAfterPing) {
        this.allowedDelayAfterPing = allowedDelayAfterPing;
    }

    public Boolean getOkFromAntispam() {
        return okFromAntispam;
    }

    public void setOkFromAntispam(Boolean okFromAntispam) {
        this.okFromAntispam = okFromAntispam;
    }

    public String getTextToPing() {
        return textToPing;
    }

    public void setTextToPing(String textToPing) {
        this.textToPing = textToPing;
    }
}
