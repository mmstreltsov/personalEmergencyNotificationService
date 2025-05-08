package ru.hse.mmstr_project.se.service.kafka.consumer;

import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.service.sender.implementations.SmsSenderLogic;

@Component
public class SmsSenderConsumer extends CommonSenderConsumer {

    protected SmsSenderConsumer(SmsSenderLogic manager) {
        super(manager);
    }
}
