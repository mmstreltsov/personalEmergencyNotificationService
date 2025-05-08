package ru.hse.mmstr_project.se.service.kafka.consumer;

import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.service.sender.implementations.EmailSenderLogic;

@Component
public class EmailSenderConsumer extends CommonSenderConsumer {

    protected EmailSenderConsumer(EmailSenderLogic manager) {
        super(manager);
    }
}
