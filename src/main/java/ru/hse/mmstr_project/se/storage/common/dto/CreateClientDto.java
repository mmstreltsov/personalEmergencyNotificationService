package ru.hse.mmstr_project.se.storage.common.dto;

import java.util.List;

public class CreateClientDto {
    private String name;
    private String telegramId;
    private Long chatId;
    private List<FriendDto> listOfFriends;

    public CreateClientDto() {
    }

    public CreateClientDto(String name, String telegramId, Long chatId, List<FriendDto> listOfFriends) {
        this.name = name;
        this.telegramId = telegramId;
        this.chatId = chatId;
        this.listOfFriends = listOfFriends;
    }

    public String getName() {
        return name;
    }

    public String getTelegramId() {
        return telegramId;
    }

    public Long getChatId() {
        return chatId;
    }

    public List<FriendDto> getListOfFriends() {
        return listOfFriends;
    }

    @Override
    public String toString() {
        return "CreateClientDto{" +
                "name='" + name + '\'' +
                ", telegramId='" + telegramId + '\'' +
                ", chatId=" + chatId +
                ", listOfFriends=" + listOfFriends +
                '}';
    }
}