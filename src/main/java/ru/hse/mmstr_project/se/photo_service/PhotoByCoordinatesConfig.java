package ru.hse.mmstr_project.se.photo_service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;

@Configuration
public class PhotoByCoordinatesConfig {

    @Bean
    public HttpClient httpClientForMapbox() {
        return HttpClient.newBuilder().build();
    }
}
