package ru.hse.mmstr_project.se.service.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import ru.hse.mmstr_project.se.kafka.BaseConsumerConfig;
import ru.hse.mmstr_project.se.service.kafka.consumer.MetaRequestConsumer;

@Configuration
@Import({
        BaseConsumerConfig.class,
})
public class ConsumersContainers {

    @Bean
    public KafkaMessageListenerContainer<String, Object> metaRequestContainer(
            ConcurrentKafkaListenerContainerFactory<String, Object> factory,
            MetaRequestConsumer consumer,
            @Value("${kafka.topic.name.meta-requests}") String topic) {

        ContainerProperties props = new ContainerProperties(topic);
        props.setGroupId(topic + "_1");
        props.setMessageListener(consumer);
        props.setAckMode(ContainerProperties.AckMode.MANUAL);

        KafkaMessageListenerContainer<String, Object> container = new KafkaMessageListenerContainer<>(
                factory.getConsumerFactory(),
                props);
        container.setAutoStartup(true);
        return container;
    }
}
