package ru.hse.mmstr_project.se.storage.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Scenario")
public class Scenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false)
    private UUID uuid;

    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(name = "clientId")
    private Long clientId;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "friendIds", columnDefinition = "LONG[]")
    private List<Long> friendsIds;

    @Column(name = "firstTimeToActivate")
    private Instant firstTimeToActivate;

    @Column(name = "firstTimeToActivateOrigin")
    private Instant firstTimeToActivateOrigin;

    @Column(name = "allowedDelayAfterPing")
    private Integer allowedDelayAfterPing;

    @Column(name = "okFromAntispam")
    private Boolean okFromAntispam;

    @Column(name = "okByHand")
    private Boolean okByHand;

    @Column(name = "textToPing", columnDefinition = "TEXT")
    private String textToPing;

    public Scenario() {
    }

    public Scenario(
            Long id,
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
        this.id = id;
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

    public Instant getFirstTimeToActivate() {
        return firstTimeToActivate;
    }

    public void setFirstTimeToActivate(Instant firstTimeToActivate) {
        this.firstTimeToActivate = firstTimeToActivate;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getFirstTimeToActivateOrigin() {
        return firstTimeToActivateOrigin;
    }

    public void setFirstTimeToActivateOrigin(Instant firstTimeToActivateOrigin) {
        this.firstTimeToActivateOrigin = firstTimeToActivateOrigin;
    }

    public Boolean getOkByHand() {
        return okByHand;
    }

    public void setOkByHand(Boolean okByHand) {
        this.okByHand = okByHand;
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