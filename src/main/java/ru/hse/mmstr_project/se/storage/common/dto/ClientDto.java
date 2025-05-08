package ru.hse.mmstr_project.se.storage.common.dto;

import java.util.List;

public class ClientDto {
    private Long id;
    private String name;
    private String telegramId;
    private Long chatId;
    private List<FriendDto> listOfFriends;

    public ClientDto() {
    }

    public ClientDto(Long id, String name, String telegramId, Long chatId, List<FriendDto> listOfFriends) {
        this.name = name;
        this.id = id;
        this.telegramId = telegramId;
        this.chatId = chatId;
        this.listOfFriends = listOfFriends;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTelegramId(String telegramId) {
        this.telegramId = telegramId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public void setListOfFriends(List<FriendDto> listOfFriends) {
        this.listOfFriends = listOfFriends;
    }

    @Override
    public String toString() {
        return "ClientDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", telegramId='" + telegramId + '\'' +
                ", chatId=" + chatId +
                ", listOfFriends=" + listOfFriends +
                '}';
    }
}