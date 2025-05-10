package ru.hse.mmstr_project.se.service.sender.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;

@Configuration
public class SenderLogicContextConfiguration {

    @Bean
    public ObjectMapper objectMapperForSender() {
        return new ObjectMapper();
    }

    @Bean
    public HttpClient httpClientForSms() {
        return HttpClient.newHttpClient();
    }
}
