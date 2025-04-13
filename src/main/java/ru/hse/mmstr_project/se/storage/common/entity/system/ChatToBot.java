package ru.hse.mmstr_project.se.storage.common.entity.system;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "chatToBots")
public class ChatToBot {

    @Id
    private Long chatId;

    @Column(name = "botId", nullable = false)
    private Long botId;

    public ChatToBot() {
    }

    public ChatToBot(Long chatId, Long botId) {
        this.chatId = chatId;
        this.botId = botId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getBotId() {
        return botId;
    }

    public void setBotId(Long name) {
        this.botId = name;
    }
}
