package ru.hse.mmstr_project.se.client.handlers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class CommandHandlersContextConfiguration {

    @Bean
    public Map<String, CommandHandler> commandHandlers(List<CommandHandler> commandHandlers) {
        return commandHandlers.stream().collect(Collectors.toMap(
                CommandHandler::getCommand,
                Function.identity()));
    }
}
