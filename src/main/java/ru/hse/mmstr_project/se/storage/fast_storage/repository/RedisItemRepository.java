package ru.hse.mmstr_project.se.storage.fast_storage.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import ru.hse.mmstr_project.se.storage.fast_storage.dto.IncidentMetadataDto;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
public class RedisItemRepository {

    private static final String KEY_PREFIX = "incident:";
    private static final String TIME_SORTED_SET = "incident:time-index";
    private static final String DEDUP_SET = "dedup:";

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisItemRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveAll(List<IncidentMetadataDto> entities) {
        Map<String, IncidentMetadataDto> entityMap = new HashMap<>();
        for (IncidentMetadataDto entity : entities) {
            String id = entity.id().toString();
            entityMap.put(KEY_PREFIX + id, entity);
        }

        if (entityMap.isEmpty()) {
            return;
        }
        redisTemplate.opsForValue().multiSet(entityMap);

        entityMap.forEach((key, value) -> {
            redisTemplate.expire(key, value.allowedDelayAfterPing() + 5, TimeUnit.MINUTES);
            redisTemplate.opsForZSet().add(
                    TIME_SORTED_SET,
                    value.id(),
                    value.firstTimeToActivate());
        });
    }

    public void save(IncidentMetadataDto entity) {
        saveAll(Collections.singletonList(entity));
    }

    public void removeAll(List<IncidentMetadataDto> entities) {
        List<Long> keys = entities.stream()
                .map(IncidentMetadataDto::id)
                .toList();

        redisTemplate.opsForZSet().remove(
                TIME_SORTED_SET,
                keys.toArray());

        redisTemplate.delete(keys.stream().map(it -> KEY_PREFIX + it).toList());
    }

    public Iterator<List<IncidentMetadataDto>> getIteratorByFirstTimeToActivateLessThan(
            long startTime,
            long endTime,
            int batchSize) {
        return new RedisBatchIterator<>(
                startTime,
                endTime,
                batchSize,
                this::fetchBatchFromRedis,
                this::getEntitiesByIds
        );
    }

    private Set<Long> fetchBatchFromRedis(long startTime, long endTime, long offset, int batchSize) {
        Set<Object> rawResults = redisTemplate.opsForZSet().rangeByScore(
                TIME_SORTED_SET,
                startTime,
                endTime,
                offset,
                batchSize);

        if (rawResults.isEmpty()) {
            return Collections.emptySet();
        }

        return rawResults.stream()
                .map(it -> (String) it)
                .map(Long::parseLong)
                .collect(Collectors.toSet());
    }

    public Optional<IncidentMetadataDto> findById(Long id) {
        return Optional.ofNullable((IncidentMetadataDto) redisTemplate.opsForValue().get(KEY_PREFIX + id));
    }

    private List<IncidentMetadataDto> getEntitiesByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> keys = ids.stream()
                .map(id -> KEY_PREFIX + id)
                .collect(Collectors.toList());

        List<Object> values = redisTemplate.opsForValue().multiGet(keys);

        return values.stream()
                .filter(Objects::nonNull)
                .map(v -> (IncidentMetadataDto) v)
                .collect(Collectors.toList());
    }

    public void addToDeduplicationSet(Long id, long ttlSeconds) {
        String key = DEDUP_SET + id;
        redisTemplate.opsForValue().setIfAbsent(key, "", Duration.ofSeconds(ttlSeconds));
    }

    public boolean isInDeduplicationSet(Long id) {
        String key = DEDUP_SET + id;
        return redisTemplate.hasKey(key);
    }
}