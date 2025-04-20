package ru.hse.mmstr_project.se.storage.common.dto;

import java.util.List;

public class CreateClientDto {
    private String telegramId;
    private Long chatId;
    private List<FriendDto> listOfFriends;

    public CreateClientDto() {
    }

    public CreateClientDto(String telegramId, Long chatId, List<FriendDto> listOfFriends) {
        this.telegramId = telegramId;
        this.chatId = chatId;
        this.listOfFriends = listOfFriends;
    }

    public String getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(String telegramId) {
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
                ", telegramId=" + telegramId +
                ", chatId=" + chatId +
                ", listOfFriends=" + listOfFriends +
                '}';
    }
}