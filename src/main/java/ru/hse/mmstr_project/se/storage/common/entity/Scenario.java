package ru.hse.mmstr_project.se.storage.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Scenario")
public class Scenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(name = "clientId")
    private Long clientId;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "friendIds", columnDefinition = "LONG[]")
    private List<Long> friendsIds;

    @Column(name = "firstTimeToActivate")
    private LocalDateTime firstTimeToActivate;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "listTimesToActivate", columnDefinition = "TIMESTAMP[]")
    private List<LocalDateTime> listTimesToActivate;

    @Column(name = "allowedDelayAfterPing")
    private Integer allowedDelayAfterPing;

    @Column(name = "okFromAntispam")
    private Boolean okFromAntispam;

    @Column(name = "textToPing", columnDefinition = "TEXT")
    private String textToPing;

    public Scenario() {
    }

    public Scenario(
            String text,
            Long clientId,
            List<Long> friendsIds,
            LocalDateTime firstTimeToActivate,
            List<LocalDateTime> listTimesToActivate,
            Integer allowedDelayAfterPing,
            Boolean okFromAntispam,
            String textToPing) {
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

    public void setFriendsIds(List<Long> toIds) {
        this.friendsIds = toIds;
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