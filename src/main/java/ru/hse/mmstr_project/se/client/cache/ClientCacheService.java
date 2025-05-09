package ru.hse.mmstr_project.se.client.cache;


import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class ClientCacheService {

    private final Cache<Long, Object> userSessionCache;

    public ClientCacheService(
            Cache<Long, Object> userSessionCache) {
        this.userSessionCache = userSessionCache;
    }

    public void storeEntityId(Long chatId, String entityId) {
        userSessionCache.put(chatId, entityId);
    }

    public Optional<String> getUserSession(Long chatId) {
        Object value = userSessionCache.getIfPresent(chatId);

        return Optional.of(value)
                .filter(Objects::nonNull)
                .filter(it ->  it instanceof String)
                .map(it -> (String) it);
    }
}
