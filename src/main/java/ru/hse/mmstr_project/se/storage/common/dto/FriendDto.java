package ru.hse.mmstr_project.se.storage.common.dto;

import java.util.List;
import java.util.Optional;

public class FriendDto {
    private Integer id;
    private String name;
    private List<String> wayToNotify;
    private String phoneNumber;
    private String telegramId;
    private Integer chatId;
    private String email;

    public FriendDto() {
    }

    public FriendDto(
            Integer id,
            String name,
            List<String> wayToNotify,
            String phoneNumber,
            String telegramId,
            Integer chatId,
            String email) {
        this.id = id;
        this.name = name;
        this.wayToNotify = wayToNotify;
        this.phoneNumber = phoneNumber;
        this.telegramId = telegramId;
        this.chatId = chatId;
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getWayToNotify() {
        return wayToNotify;
    }

    public void setWayToNotify(List<String> wayToNotify) {
        this.wayToNotify = wayToNotify;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(String telegramId) {
        this.telegramId = telegramId;
    }

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final FriendDto friendDto;

        private Builder() {
            friendDto = new FriendDto();
        }

        public Builder id(Integer id) {
            friendDto.id = id;
            return this;
        }

        public Builder name(String name) {
            friendDto.name = name;
            return this;
        }

        public Builder wayToNotify(List<String> wayToNotify) {
            friendDto.wayToNotify = wayToNotify;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            friendDto.phoneNumber = phoneNumber;
            return this;
        }

        public Builder telegramId(String telegramId) {
            friendDto.telegramId = telegramId;
            return this;
        }

        public Builder chatId(Integer chatId) {
            friendDto.chatId = chatId;
            return this;
        }

        public Builder email(String email) {
            friendDto.email = email;
            return this;
        }

        public FriendDto build() {
            return friendDto;
        }
    }

    public String toBeautyString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ID: ").append(id).append('\n');
        builder.append("Контакт: ").append(Optional.ofNullable(name).orElse("Имя не указано")).append('\n');

        Optional.ofNullable(wayToNotify).filter(it -> !it.isEmpty()).ifPresent(it ->
                builder.append("Предпочитаемые способы связи: ").append(String.join(", ", it)).append('\n'));

        Optional.ofNullable(phoneNumber).filter(it -> !it.isEmpty()).ifPresent(it ->
                builder.append("Телефон: ").append(it).append('\n'));

        Optional.ofNullable(telegramId).filter(it -> !it.isEmpty()).ifPresent(it ->
                builder.append("Telegram: @").append(it).append('\n'));

        Optional.ofNullable(email).filter(it -> !it.isEmpty()).ifPresent(it ->
                builder.append("Email: ").append(it).append('\n'));

        return builder.toString().trim();
    }


    @Override
    public String toString() {
        return "FriendDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", wayToNotify=" + wayToNotify +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", telegramId=" + telegramId +
                ", chatId=" + chatId +
                ", email='" + email + '\'' +
                '}';
    }
}