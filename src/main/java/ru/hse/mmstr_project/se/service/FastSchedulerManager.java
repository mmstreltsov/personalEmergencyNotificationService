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

    private static final int DUPLICATES_ALIVE_SECONDS = 60;

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
        incidentMetadataDtos = filterDuplicates(incidentMetadataDtos);

        System.out.println("HANDLED " + incidentMetadataDtos.size());
        repository.removeAll(incidentMetadataDtos.stream().limit(incidentMetadataDtos.size() / 2).toList());
    }

    private List<IncidentMetadataDto> filterDuplicates(Collection<IncidentMetadataDto> incidentMetadataDtos) {
        Map<Long, IncidentMetadataDto> collect = incidentMetadataDtos.stream()
                .collect(Collectors.toMap(IncidentMetadataDto::id, Function.identity(), (f, s) -> s));

        List<Long> result = repository.filterDuplicates(collect.keySet())
                .stream()
                .toList();
        setProcessedLikeDuplicates(result);
        fastSchedulersMetrics.incDuplicatesFiltered(result.size() - collect.keySet().size());

        return result.stream().map(collect::get).toList();
    }

    private void setProcessedLikeDuplicates(Collection<Long> incidentMetadataDtos) {
        repository.addToDeduplicationSet(incidentMetadataDtos, DUPLICATES_ALIVE_SECONDS);
    }
}
