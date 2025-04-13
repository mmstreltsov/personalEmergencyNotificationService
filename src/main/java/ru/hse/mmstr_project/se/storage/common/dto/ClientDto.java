package ru.hse.mmstr_project.se.storage.common.dto;

import java.util.List;

public class ClientDto {
    private Long id;
    private Long telegramId;
    private Long chatId;
    private List<FriendDto> listOfFriends;

    public ClientDto() {
    }

    public ClientDto(Long id, Long telegramId, Long chatId, List<FriendDto> listOfFriends) {
        this.id = id;
        this.telegramId = telegramId;
        this.chatId = chatId;
        this.listOfFriends = listOfFriends;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(Long telegramId) {
        this.telegramId = telegramId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public List<FriendDto> getListOfFriends() {
        return listOfFriends;
    }

    public void setListOfFriends(List<FriendDto> listOfFriends) {
        this.listOfFriends = listOfFriends;
    }

    @Override
    public String toString() {
        return "ClientDto{" +
                "id=" + id +
                ", telegramId=" + telegramId +
                ", chatId=" + chatId +
                ", listOfFriends=" + listOfFriends +
                '}';
    }
}