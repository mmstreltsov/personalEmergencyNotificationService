package ru.hse.mmstr_project.se.storage.common.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientMapperContext {

    @Bean
    ClientMapper clientMapper() {
        return Mappers.getMapper(ClientMapper.class);
    }
}
