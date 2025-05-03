package ru.hse.mmstr_project.se.service;

import org.springframework.stereotype.Service;
import ru.hse.mmstr_project.se.shedulers.metrics.FastSchedulersMetrics;
import ru.hse.mmstr_project.se.storage.fast_storage.dto.IncidentMetadataDto;
import ru.hse.mmstr_project.se.storage.fast_storage.repository.RedisItemRepository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FastSchedulerManager {

    public static final int DUPLICATES_ALIVE_SECONDS = 180;

    private final RedisItemRepository repository;
    private final FastSchedulersMetrics fastSchedulersMetrics;

    public FastSchedulerManager(
            RedisItemRepository repository,
            FastSchedulersMetrics fastSchedulersMetrics) {
        this.repository = repository;
        this.fastSchedulersMetrics = fastSchedulersMetrics;
    }

    public void handle(Collection<IncidentMetadataDto> incidentMetadataDtos) {
        fastSchedulersMetrics.measureRequest(() -> handleI(incidentMetadataDtos));
    }

    private void handleI(Collection<IncidentMetadataDto> incidentMetadataDtos) {
        List<IncidentMetadataDto> sendToUsers = filterDuplicates(incidentMetadataDtos);
        // kafka

        repository.removeAll(incidentMetadataDtos.stream().toList());
    }

    private List<IncidentMetadataDto> filterDuplicates(Collection<IncidentMetadataDto> incidentMetadataDtos) {
        Map<String, IncidentMetadataDto> collect = incidentMetadataDtos.stream()
                .collect(Collectors.toMap(IncidentMetadataDto::id, Function.identity(), (f, s) -> s));

        List<String> result = repository.filterDuplicates(collect.keySet())
                .stream()
                .toList();
        setProcessedLikeDuplicates(result);
        fastSchedulersMetrics.incDuplicatesFiltered(collect.keySet().size() - result.size());

        return result.stream().map(collect::get).toList();
    }

    private void setProcessedLikeDuplicates(Collection<String> incidentMetadataDtos) {
        repository.addToDeduplicationSet(incidentMetadataDtos, DUPLICATES_ALIVE_SECONDS);
    }
}
