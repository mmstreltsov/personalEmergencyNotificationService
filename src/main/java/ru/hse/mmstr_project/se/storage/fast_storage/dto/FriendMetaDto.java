package ru.hse.mmstr_project.se.storage.fast_storage.dto;

import ru.hse.mmstr_project.se.storage.common.dto.FriendDto;

import java.util.List;

public final class FriendMetaDto {
    private Integer id;
    private String name;
    private List<String> wayToNotify;
    private String phoneNumber;
    private Integer telegramId;
    private Integer chatId;
    private String email;

    public FriendMetaDto() {
    }

    public FriendMetaDto(
            Integer id,
            String name,
            List<String> wayToNotify,
            String phoneNumber,
            Integer telegramId,
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

    public static FriendMetaDto parse(FriendDto friendDto) {
        return new FriendMetaDto(
                friendDto.getId(),
                friendDto.getName(),
                friendDto.getWayToNotify(),
                friendDto.getPhoneNumber(),
                friendDto.getTelegramId(),
                friendDto.getChatId(),
                friendDto.getEmail());
    }

    public Integer id() {
        return id;
    }

    public String name() {
        return name;
    }

    public List<String> wayToNotify() {
        return wayToNotify;
    }

    public String phoneNumber() {
        return phoneNumber;
    }

    public Integer telegramId() {
        return telegramId;
    }

    public Integer chatId() {
        return chatId;
    }

    public String email() {
        return email;
    }
}
