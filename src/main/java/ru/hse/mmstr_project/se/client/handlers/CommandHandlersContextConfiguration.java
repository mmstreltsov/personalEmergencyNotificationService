package ru.hse.mmstr_project.se.client.handlers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.hse.mmstr_project.se.client.handlers.impl.HelpHandler;

import java.util.Map;

@Configuration
public class CommandHandlersContextConfiguration {

    @Bean
    public Map<String, CommandHandler> commandHandlers(
            HelpHandler helpHandler) {
        return Map.of("/help", helpHandler);
    }
}
