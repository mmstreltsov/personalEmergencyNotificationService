package ru.hse.mmstr_project.se.storage.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "telegramId", unique = true)
    private String telegramId;

    @Column(name = "chatId")
    private Long chatId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "listOfFriends", columnDefinition = "jsonb")
    private List<Friend> listOfFriends;

    public Client() {}

    public Client(String telegramId, Long chatId, List<Friend> listOfFriends) {
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

    public List<Friend> getListOfFriends() {
        return listOfFriends;
    }

    public void setListOfFriends(List<Friend> listOfFriends) {
        this.listOfFriends = listOfFriends;
    }
}