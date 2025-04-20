package ru.hse.mmstr_project.se.storage.common.dto;

import java.time.Instant;
import java.util.List;

public class ScenarioDto {

    private Long id;
    private String text;
    private Long clientId;
    private List<Long> friendsIds;
    private Instant firstTimeToActivate;
    private List<Instant> listTimesToActivate;
    private Integer allowedDelayAfterPing;
    private Boolean okFromAntispam;
    private String textToPing;

    public ScenarioDto() {
    }

    private ScenarioDto(Builder builder) {
        this(
                builder.id,
                builder.text,
                builder.clientId,
                builder.friendsIds,
                builder.firstTimeToActivate,
                builder.listTimesToActivate,
                builder.allowedDelayAfterPing,
                builder.okFromAntispam,
                builder.textToPing);
    }

    public ScenarioDto(
            Long id,
            String text,
            Long clientId,
            List<Long> friendsIds,
            Instant firstTimeToActivate,
            List<Instant> listTimesToActivate,
            Integer allowedDelayAfterPing,
            Boolean okFromAntispam,
            String textToPing) {
        this.id = id;
        this.text = text;
        this.clientId = clientId;
        this.friendsIds = friendsIds;
        this.firstTimeToActivate = firstTimeToActivate;
        this.listTimesToActivate = listTimesToActivate;
        this.allowedDelayAfterPing = allowedDelayAfterPing;
        this.okFromAntispam = okFromAntispam;
        this.textToPing = textToPing;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<Long> getFriendsIds() {
        return friendsIds;
    }

    public void setFriendsIds(List<Long> friendsIds) {
        this.friendsIds = friendsIds;
    }

    public Instant getFirstTimeToActivate() {
        return firstTimeToActivate;
    }

    public void setFirstTimeToActivate(Instant firstTimeToActivate) {
        this.firstTimeToActivate = firstTimeToActivate;
    }

    public List<Instant> getListTimesToActivate() {
        return listTimesToActivate;
    }

    public void setListTimesToActivate(List<Instant> listTimesToActivate) {
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

    public Builder toBuilder() {
        return new Builder()
                .id(this.id)
                .text(this.text)
                .clientId(this.clientId)
                .friendsIds(this.friendsIds)
                .firstTimeToActivate(this.firstTimeToActivate)
                .listTimesToActivate(this.listTimesToActivate)
                .allowedDelayAfterPing(this.allowedDelayAfterPing)
                .okFromAntispam(this.okFromAntispam)
                .textToPing(this.textToPing);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String text;
        private Long clientId;
        private List<Long> friendsIds;
        private Instant firstTimeToActivate;
        private List<Instant> listTimesToActivate;
        private Integer allowedDelayAfterPing;
        private Boolean okFromAntispam;
        private String textToPing;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder clientId(Long clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder friendsIds(List<Long> friendsIds) {
            this.friendsIds = friendsIds;
            return this;
        }

        public Builder firstTimeToActivate(Instant firstTimeToActivate) {
            this.firstTimeToActivate = firstTimeToActivate;
            return this;
        }

        public Builder listTimesToActivate(List<Instant> listTimesToActivate) {
            this.listTimesToActivate = listTimesToActivate;
            return this;
        }

        public Builder allowedDelayAfterPing(Integer allowedDelayAfterPing) {
            this.allowedDelayAfterPing = allowedDelayAfterPing;
            return this;
        }

        public Builder okFromAntispam(Boolean okFromAntispam) {
            this.okFromAntispam = okFromAntispam;
            return this;
        }

        public Builder textToPing(String textToPing) {
            this.textToPing = textToPing;
            return this;
        }

        public ScenarioDto build() {
            return new ScenarioDto(this);
        }
    }


    @Override
    public String toString() {
        return "ScenarioDto{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", clientId=" + clientId +
                ", friendsIds=" + friendsIds +
                ", firstTimeToActivate=" + firstTimeToActivate +
                ", listTimesToActivate=" + listTimesToActivate +
                ", allowedDelayAfterPing=" + allowedDelayAfterPing +
                ", okFromAntispam=" + okFromAntispam +
                ", textToPing='" + textToPing + '\'' +
                '}';
    }
}