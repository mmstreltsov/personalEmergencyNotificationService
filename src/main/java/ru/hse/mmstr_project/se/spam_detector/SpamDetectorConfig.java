package ru.hse.mmstr_project.se.spam_detector;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class SpamDetectorConfig {
    @Bean
    public ExecutorService executorServiceForSpamDetector() {
        return Executors.newCachedThreadPool();
    }

    @Bean
    public ObjectMapper objectMapperForSpamDetector() {
        return new ObjectMapper();
    }

    @Bean
    public HttpClient httpClientForMl() {
        return HttpClient.newHttpClient();
    }
}
