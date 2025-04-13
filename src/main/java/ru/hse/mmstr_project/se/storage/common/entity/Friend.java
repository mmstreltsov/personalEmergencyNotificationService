package ru.hse.mmstr_project.se.storage.common.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Friend {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("aWayToNotify")
    private List<String> aWayToNotify;
    @JsonProperty("phoneNumber")
    private String phoneNumber;
    @JsonProperty("telegramId")
    private Integer telegramId;
    @JsonProperty("chatId")
    private Integer chatId;
    @JsonProperty("email")
    private String email;

    public Friend() {
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

    public List<String> getAWayToNotify() {
        return aWayToNotify;
    }

    public void setAWayToNotify(List<String> aWayToNotify) {
        this.aWayToNotify = aWayToNotify;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(Integer telegramId) {
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
}