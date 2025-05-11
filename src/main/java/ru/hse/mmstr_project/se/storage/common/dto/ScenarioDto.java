package ru.hse.mmstr_project.se.storage.common.dto;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ScenarioDto {

    private Long id;
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

    public ScenarioDto() {
    }

    private ScenarioDto(Builder builder) {
        this(
                builder.id,
                builder.uuid,
                builder.name,
                builder.text,
                builder.clientId,
                builder.friendsIds,
                builder.firstTimeToActivate,
                builder.firstTimeToActivateOrigin,
                builder.allowedDelayAfterPing,
                builder.okFromAntispam,
                builder.okByHand,
                builder.textToPing);
    }

    public ScenarioDto(
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

    public Builder toBuilder() {
        return new Builder()
                .id(this.id)
                .uuid(this.uuid)
                .name(this.name)
                .text(this.text)
                .clientId(this.clientId)
                .friendsIds(this.friendsIds)
                .firstTimeToActivate(this.firstTimeToActivate)
                .firstTimeToActivateOrigin(this.firstTimeToActivateOrigin)
                .allowedDelayAfterPing(this.allowedDelayAfterPing)
                .okFromAntispam(this.okFromAntispam)
                .okByHand(this.okByHand)
                .textToPing(this.textToPing);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
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

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder uuid(UUID id) {
            this.uuid = id;
            return this;
        }

        public Builder name(String text) {
            this.name = text;
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

        public Builder firstTimeToActivateOrigin(Instant firstTimeToActivate) {
            this.firstTimeToActivateOrigin = firstTimeToActivate;
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

        public Builder okByHand(Boolean okByHand) {
            this.okByHand = okByHand;
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

    public String toBeautyString() {
        StringBuilder builder = new StringBuilder();

        builder.append("ID: ").append("`").append(uuid.toString()).append("`").append('\n');

        builder.append("Название: ")
                .append(Optional.ofNullable(name).filter(it -> !it.isEmpty()).orElse("Не указано"))
                .append('\n');

        builder.append("Текст: ")
                .append(Optional.ofNullable(text).filter(it -> !it.isEmpty()).orElse("Отсутствует"))
                .append('\n');

        Optional.ofNullable(friendsIds).filter(it -> !it.isEmpty()).ifPresent(it -> {
            builder.append("Контакты ID: ").append(String.join(", ",
                            it.stream().map(String::valueOf).toList()))
                    .append('\n');
        });

        Optional.ofNullable(allowedDelayAfterPing).ifPresent(it ->
                builder.append("Допустимая задержка после пинга: ")
                        .append(it)
                        .append(" с")
                        .append('\n')
        );

        builder.append("Проверка антиспам: ")
                .append(Optional.ofNullable(okFromAntispam).orElse(true) ? "Пройдена" : "Не пройдена")
                .append('\n');

        builder.append("Статус: ")
                .append(Optional.ofNullable(okByHand).orElse(true) ? "Активный" : "Неактивный")
                .append('\n');

        Optional.ofNullable(textToPing).filter(it -> !it.isEmpty()).ifPresent(it ->
                builder.append("Текст для пинга: ")
                        .append(it)
                        .append('\n')
        );

        return builder.toString();
    }

    public static String timesToString(Collection<Instant> times) {
        return "Время срабатывания (в UTC):\n" +
                "`" +
                String.join(" ", times.stream()
                        .map(Instant::toString)
                        .toList()) +
                "`";
    }

    @Override
    public String toString() {
        return "ScenarioDto{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", clientId=" + clientId +
                ", friendsIds=" + friendsIds +
                ", firstTimeToActivate=" + firstTimeToActivate +
                ", firstTimeToActivateOrigin=" + firstTimeToActivateOrigin +
                ", allowedDelayAfterPing=" + allowedDelayAfterPing +
                ", okFromAntispam=" + okFromAntispam +
                ", okByHand=" + okByHand +
                ", textToPing='" + textToPing + '\'' +
                '}';
    }
}