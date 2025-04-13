package ru.hse.mmstr_project.se.storage.common.repository.system;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hse.mmstr_project.se.storage.common.entity.system.ChatToBot;

@Repository
public interface ChatToBotBotRepository extends JpaRepository<ChatToBot, Long> {
}