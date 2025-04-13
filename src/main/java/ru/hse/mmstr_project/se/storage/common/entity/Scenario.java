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

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "toIds", columnDefinition = "LONG[]")
    private List<Long> toIds;

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

    public Scenario(String text,
                    List<Long> toIds,
                    LocalDateTime firstTimeToActivate,
                    List<LocalDateTime> listTimesToActivate,
                    Integer allowedDelayAfterPing,
                    Boolean okFromAntispam,
                    String textToPing) {
        this.text = text;
        this.toIds = toIds;
        this.firstTimeToActivate = firstTimeToActivate;
        this.listTimesToActivate = listTimesToActivate;
        this.allowedDelayAfterPing = allowedDelayAfterPing;
        this.okFromAntispam = okFromAntispam;
        this.textToPing = textToPing;
    }

    // Геттеры и сеттеры
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
}