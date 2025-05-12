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
    private static final String CONFIRM_KEY_PREFIX = "incident:";
    private static final String TIME_SORTED_SET = "incident:time-index:";
    private static final String DEDUP_SET = "dedup:set:";
    private static final String TEMP_CHECK = "temp:check:";

    private static final Integer SHARD_SIZE = 65536;

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisItemRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
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

        Map<String, Long> zsetEntries = entityMap.values().stream()
                .collect(Collectors.toMap(
                        IncidentMetadataDto::id,
                        incidentMetadataDto -> incidentMetadataDto.firstTimeToActivate()
                                + incidentMetadataDto.allowedDelayAfterPing() * 1_000));

        Long lastIndexNumber = fetchCurrentIndexes().stream()
                .map(it -> it.substring(TIME_SORTED_SET.length()))
                .map(Long::parseLong)
                .reduce(0L, (a, b) -> a > b ? a : b);

        Long size = redisTemplate.opsForZSet().size(TIME_SORTED_SET + lastIndexNumber);
        if (Objects.nonNull(size) && size > SHARD_SIZE) {
            lastIndexNumber++;
        }

        String index = TIME_SORTED_SET + lastIndexNumber;
        redisTemplate.executePipelined(new SessionCallback<>() {
            @Override
            @SuppressWarnings("unchecked")
            public Object execute(RedisOperations operations) {

                zsetEntries.forEach((member, score) -> {
                    operations.opsForZSet().add(index, member, score);
                });

                operations.opsForValue().multiSet(entityMap);
                entityMap.forEach((key, entity) -> {
                    operations.expire(key, entity.allowedDelayAfterPing() + 5, TimeUnit.MINUTES);
                });

                return null;
            }
        });
    }

    public void saveConfirm(String key) {
        redisTemplate.opsForValue().set(CONFIRM_KEY_PREFIX + key, key, 3, TimeUnit.MINUTES);
    }

    public List<String> getConfirmKeys(List<String> key) {
        List<String> list = key.stream().map(k -> CONFIRM_KEY_PREFIX + k).toList();
        return redisTemplate.opsForValue().multiGet(list)
                .stream()
                .filter(Objects::nonNull)
                .map(it -> (String) it)
                .toList();
    }

    public void removeAll(List<IncidentMetadataDto> entities) {
        List<String> keys = entities.stream()
                .map(IncidentMetadataDto::id)
                .toList();
        if (keys.isEmpty()) {
            return;
        }

        List<String> indexes = fetchCurrentIndexes();


        redisTemplate.executePipelined(new SessionCallback<>() {
            @Override
            @SuppressWarnings("unchecked")
            public Object execute(RedisOperations operations) {
                redisTemplate.delete(keys.stream().map(it -> KEY_PREFIX + it).toList());

                Object[] keysArray = keys.toArray(new Object[0]);
                indexes.forEach(index -> redisTemplate.opsForZSet().remove(index, keysArray));
                return null;
            }
        });

        removeEmptyIndexes(indexes);
    }

    public void remove(String key) {
        List<String> indexes = fetchCurrentIndexes();

        redisTemplate.executePipelined(new SessionCallback<>() {
            @Override
            @SuppressWarnings("unchecked")
            public Object execute(RedisOperations operations) {
                operations.delete(key);

                Object[] keysArray = {key};
                indexes.forEach(index -> redisTemplate.opsForZSet().remove(index, keysArray));
                return null;
            }
        });
    }

    private void removeEmptyIndexes(List<String> keys) {
        for (String key : keys) {
            Long size = redisTemplate.opsForZSet().size(key);
            if (Objects.isNull(size) || size == 0) {
                redisTemplate.delete(key);
            }
        }
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
                this::getEntitiesByIds,
                this::fetchCurrentIndexes);
    }

    private List<String> fetchCurrentIndexes() {
        return redisTemplate.keys(TIME_SORTED_SET + "*").stream().toList();
    }

    private Set<String> fetchBatchFromRedis(long startTime, String indexId, long endTime, long offset, int batchSize) {
        Set<Object> rawResults = redisTemplate.opsForZSet().rangeByScore(
                indexId,
                startTime,
                endTime,
                offset,
                batchSize);

        if (rawResults.isEmpty()) {
            return Collections.emptySet();
        }

        return rawResults.stream()
                .map(it -> (String) it)
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
        redisTemplate.opsForSet().add(setKey, ids.toArray(new String[0]));
        redisTemplate.expire(setKey, ttlSeconds, TimeUnit.SECONDS);
    }

    public boolean isInDuplicates(String id) {
        for (String setKey : redisTemplate.keys(DEDUP_SET + "*")) {
            if (redisTemplate.opsForSet().isMember(setKey, id)) {
                return true;
            }
        }
        return false;
    }

    public List<String> filterDuplicates(Collection<String> ids) {
        Set<String> setKeys = redisTemplate.keys(DEDUP_SET + "*");

        if (setKeys.isEmpty()) {
            return new ArrayList<>(ids);
        }

        String tempSetKey = TEMP_CHECK + UUID.randomUUID();

        try {
            redisTemplate.opsForSet().add(tempSetKey, ids.toArray(new String[0]));

            Set<String> blacklist = new HashSet<>();
            for (String setKey : setKeys) {
                blacklist.addAll(redisTemplate.opsForSet().intersect(tempSetKey, setKey)
                        .stream()
                        .map(it -> (String) it)
                        .toList());
            }

            Set<String> remainingIdStrings = redisTemplate.opsForSet().members(tempSetKey).stream()
                    .map(it -> (String) it)
                    .collect(Collectors.toSet());
            remainingIdStrings.removeAll(blacklist);

            return remainingIdStrings.stream().toList();
        } finally {
            redisTemplate.delete(tempSetKey);
        }
    }
}