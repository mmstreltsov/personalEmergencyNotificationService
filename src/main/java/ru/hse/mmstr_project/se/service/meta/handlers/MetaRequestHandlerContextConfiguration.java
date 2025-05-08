package ru.hse.mmstr_project.se.service.meta.handlers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.hse.mmstr_project.se.service.meta.MessageType;

import java.util.Map;

@Configuration
public class MetaRequestHandlerContextConfiguration {

    @Bean
    public Map<MessageType, MetaRequestHandler> handlers() {
        return Map.of();
    }
}
