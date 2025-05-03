package ru.hse.mmstr_project.se.storage.fast_storage.repository;

import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Repository;
import ru.hse.mmstr_project.se.storage.fast_storage.dto.IncidentMetadataDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class RedisItemRepository {

    private static final String KEY_PREFIX = "incident:";
    private static final String TIME_SORTED_SET = "incident:time-index";
    private static final String DEDUP_SET = "dedup:set:";
    private static final String TEMP_CHECK = "temp:check:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, String> stringRedisTemplate;

    public RedisItemRepository(
            RedisTemplate<String, Object> redisTemplate,
            RedisTemplate<String, String> stringRedisTemplate) {
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void saveAll(List<IncidentMetadataDto> entities) {
        if (entities.isEmpty()) {
            return;
        }

        Map<String, IncidentMetadataDto> entityMap = entities.stream()
                .collect(Collectors.toMap(
                        e -> KEY_PREFIX + e.id(),
                        Function.identity(),
                        (f, s) -> s));

        Map<String, Long> zsetEntries = entityMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        it -> it.getValue().firstTimeToActivate()));

        stringRedisTemplate.executePipelined((new SessionCallback<>() {
            @Override
            @SuppressWarnings("unchecked")
            public Object execute(RedisOperations operations) {
                zsetEntries.forEach((member, score) -> {
                    operations.opsForZSet().add(TIME_SORTED_SET, member, score);
                });

                return null;
            }
        }));

        redisTemplate.executePipelined(new SessionCallback<>() {
            @Override
            @SuppressWarnings("unchecked")
            public Object execute(RedisOperations operations) {
                operations.opsForValue().multiSet(entityMap);
                entityMap.forEach((key, entity) -> {
                    operations.expire(key, entity.allowedDelayAfterPing() + 5, TimeUnit.MINUTES);
                });

                return null;
            }
        });
    }

    public void save(IncidentMetadataDto entity) {
        saveAll(Collections.singletonList(entity));
    }

    public void removeAll(List<IncidentMetadataDto> entities) {
        List<String> keys = entities.stream()
                .map(IncidentMetadataDto::id)
                .toList();
        if (keys.isEmpty()) {
            return;
        }

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

    private Set<String> fetchBatchFromRedis(long startTime, long endTime, long offset, int batchSize) {
        Set<String> rawResults = stringRedisTemplate.opsForZSet().rangeByScore(
                TIME_SORTED_SET,
                startTime,
                endTime,
                offset,
                batchSize);
        System.out.println("FETCHED RESULTS: " + rawResults.size());

        return rawResults.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public List<IncidentMetadataDto> getEntitiesByIds(List<String> ids) {
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> keys = ids.stream()
                .map(id -> KEY_PREFIX + id)
                .collect(Collectors.toList());

        List<Object> values = redisTemplate.opsForValue().multiGet(keys);
        System.out.println("RESULTS: " + values.size());

        return values.stream()
                .filter(Objects::nonNull)
                .map(v -> (IncidentMetadataDto) v)
                .collect(Collectors.toList());
    }

    public void addToDeduplicationSet(Collection<String> ids, long ttlSeconds) {
        if (ids.isEmpty()) {
            return;
        }
        String setKey = DEDUP_SET + System.currentTimeMillis();
        stringRedisTemplate.opsForSet().add(setKey, ids.toArray(new String[0]));
        stringRedisTemplate.expire(setKey, ttlSeconds, TimeUnit.SECONDS);
    }

    public List<String> filterDuplicates(Collection<String> ids) {
        Set<String> setKeys = stringRedisTemplate.keys(DEDUP_SET + "*");

        if (setKeys.isEmpty()) {
            return new ArrayList<>(ids);
        }

        String tempSetKey = TEMP_CHECK + UUID.randomUUID();

        try {
            stringRedisTemplate.opsForSet().add(tempSetKey, ids.toArray(new String[0]));

            Set<String> blacklist = new HashSet<>();
            for (String setKey : setKeys) {
                blacklist.addAll(stringRedisTemplate.opsForSet().difference(tempSetKey, setKey)
                        .stream()
                        .map(it -> (String) it)
                        .toList());
            }

            Set<String> remainingIdStrings = new HashSet<>(stringRedisTemplate.opsForSet().members(tempSetKey));
            remainingIdStrings.removeAll(blacklist);

            return remainingIdStrings.stream().toList();
        } finally {
            stringRedisTemplate.delete(tempSetKey);
        }
    }
}