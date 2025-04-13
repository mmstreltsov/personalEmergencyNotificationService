package ru.hse.mmstr_project.se.storage.common.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ScenarioDto {

    private Long id;
    private String text;
    private List<Long> toIds;
    private LocalDateTime firstTimeToActivate;
    private List<LocalDateTime> listTimesToActivate;
    private Integer allowedDelayAfterPing;
    private Boolean okFromAntispam;
    private String textToPing;

    public ScenarioDto() {
    }

    public ScenarioDto(
            Long id,
            String text,
            List<Long> toIds,
            LocalDateTime firstTimeToActivate,
            List<LocalDateTime> listTimesToActivate,
            Integer allowedDelayAfterPing,
            Boolean okFromAntispam,
            String textToPing) {
        this.id = id;
        this.text = text;
        this.toIds = toIds;
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

    public List<Long> getToIds() {
        return toIds;
    }

    public void setToIds(List<Long> toIds) {
        this.toIds = toIds;
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

    @Override
    public String toString() {
        return "ScenarioDto{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", toIds=" + toIds +
                ", firstTimeToActivate=" + firstTimeToActivate +
                ", listTimesToActivate=" + listTimesToActivate +
                ", allowedDelayAfterPing=" + allowedDelayAfterPing +
                ", okFromAntispam=" + okFromAntispam +
                ", textToPing='" + textToPing + '\'' +
                '}';
    }
}