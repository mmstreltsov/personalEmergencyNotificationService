package ru.hse.mmstr_project.se.service.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import ru.hse.mmstr_project.se.kafka.dto.SenderRequestDto;
import ru.hse.mmstr_project.se.service.sender.implementations.CommonSenderLogic;

public abstract class CommonSenderConsumer implements AcknowledgingMessageListener<String, Object> {

    protected final CommonSenderLogic manager;

    protected CommonSenderConsumer(CommonSenderLogic manager) {
        this.manager = manager;
    }

    @Override
    public void onMessage(ConsumerRecord<String, Object> record, Acknowledgment acknowledgment) {
        Object value = record.value();
        if (!(value instanceof SenderRequestDto) || manager.sendMessage((SenderRequestDto) value)) {
            acknowledgment.acknowledge();
        }
    }
}
