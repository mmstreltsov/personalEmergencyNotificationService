package ru.hse.mmstr_project.se.service.meta.handlers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.hse.mmstr_project.se.service.meta.EntityType;
import ru.hse.mmstr_project.se.service.meta.FunctionType;
import ru.hse.mmstr_project.se.service.meta.MessageType;
import ru.hse.mmstr_project.se.service.meta.handlers.impl.CreateClientHandlerImpl;
import ru.hse.mmstr_project.se.service.meta.handlers.impl.CreateScenarioHandlerImpl;
import ru.hse.mmstr_project.se.service.meta.handlers.impl.DeleteClientHandlerImpl;
import ru.hse.mmstr_project.se.service.meta.handlers.impl.DeleteScenarioHandlerImpl;
import ru.hse.mmstr_project.se.service.meta.handlers.impl.ReadClientHandlerImpl;
import ru.hse.mmstr_project.se.service.meta.handlers.impl.ReadScenarioHandlerImpl;
import ru.hse.mmstr_project.se.service.meta.handlers.impl.UpdateClientHandlerImpl;
import ru.hse.mmstr_project.se.service.meta.handlers.impl.UpdateScenarioHandlerImpl;

import java.util.Map;

@Configuration
public class MetaRequestHandlerContextConfiguration {

    @Bean
    public Map<MessageType, MetaRequestHandler> handlers(
            CreateScenarioHandlerImpl createScenarioHandler,
            CreateClientHandlerImpl createClientHandler,
            DeleteScenarioHandlerImpl deleteScenarioHandler,
            DeleteClientHandlerImpl deleteClientHandler,
            UpdateScenarioHandlerImpl updateScenarioHandler,
            UpdateClientHandlerImpl updateClientHandler,
            ReadScenarioHandlerImpl readScenarioHandler,
            ReadClientHandlerImpl readClientHandler) {

        return Map.of(
                new MessageType(EntityType.CLIENT, FunctionType.CREATE), createClientHandler,
                new MessageType(EntityType.CLIENT, FunctionType.UPDATE), updateClientHandler,
                new MessageType(EntityType.CLIENT, FunctionType.READ), readClientHandler,
                new MessageType(EntityType.CLIENT, FunctionType.DELETE), deleteClientHandler,
                new MessageType(EntityType.SCENARIO, FunctionType.CREATE), createScenarioHandler,
                new MessageType(EntityType.SCENARIO, FunctionType.UPDATE), updateScenarioHandler,
                new MessageType(EntityType.SCENARIO, FunctionType.READ), readScenarioHandler,
                new MessageType(EntityType.SCENARIO, FunctionType.DELETE), deleteScenarioHandler);
    }
}
