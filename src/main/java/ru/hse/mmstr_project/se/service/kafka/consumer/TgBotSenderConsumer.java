package ru.hse.mmstr_project.se.service.kafka.consumer;

import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.service.sender.implementations.TgBotSenderLogic;

@Component
public class TgBotSenderConsumer extends CommonSenderConsumer {

    protected TgBotSenderConsumer(TgBotSenderLogic manager) {
        super(manager);
    }
}
