package ru.hse.mmstr_project.se.storage.fast_storage.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import ru.hse.mmstr_project.se.storage.fast_storage.dto.IncidentMetadataDto;

import java.util.ArrayList;
import java.util.HashMap;
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

        if (!entityMap.isEmpty()) {
            redisTemplate.opsForValue().multiSet(entityMap);

            for (String key : entityMap.keySet()) {
                redisTemplate.expire(key, 1, TimeUnit.HOURS);
            }
        }
        for (IncidentMetadataDto entity : entities) {
            redisTemplate.opsForZSet().add(
                    TIME_SORTED_SET,
                    entity.id().toString(),
                    entity.firstTimeToActivate());
        }
        redisTemplate.expire(TIME_SORTED_SET, 1, TimeUnit.HOURS);
    }

    public void save(IncidentMetadataDto entity) {
        String id = entity.id().toString();

        redisTemplate.opsForValue().set(KEY_PREFIX + id, entity);
        redisTemplate.expire(KEY_PREFIX + id, 1, TimeUnit.HOURS);

        redisTemplate.opsForZSet().add(
                TIME_SORTED_SET,
                entity.id().toString(),
                entity.firstTimeToActivate()
        );
    }

    public List<IncidentMetadataDto> findByFirstTimeToActivateLessThan(long thresholdTime) {
        Set<String> idStrings = redisTemplate.opsForZSet().rangeByScore(
                        TIME_SORTED_SET,
                        0,
                        thresholdTime - 1)
                .stream()
                .map(it -> (String) it)
                .collect(Collectors.toSet());

        List<Long> ids = idStrings.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());

        return getEntitiesByIds(ids);
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
}