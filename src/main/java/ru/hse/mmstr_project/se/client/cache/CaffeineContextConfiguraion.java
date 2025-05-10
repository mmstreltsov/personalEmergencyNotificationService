package ru.hse.mmstr_project.se.client.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineContextConfiguraion {

    @Bean
    public Cache<Long, Object> userSessionCache(
            @Value("${cache.user-session.expiration-hours:2}") long expirationHours,
            @Value("${cache.user-session.maximum-size:1000}") long maximumSize) {
        return Caffeine.newBuilder()
                .expireAfterAccess(expirationHours, TimeUnit.HOURS)
                .maximumSize(maximumSize)
                .build();
    }
}
