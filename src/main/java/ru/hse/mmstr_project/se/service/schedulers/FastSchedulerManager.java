package ru.hse.mmstr_project.se.service.schedulers;

import org.springframework.stereotype.Service;
import ru.hse.mmstr_project.se.service.sender.SenderService;
import ru.hse.mmstr_project.se.shedulers.metrics.FastSchedulersMetrics;
import ru.hse.mmstr_project.se.storage.fast_storage.dto.IncidentMetadataDto;
import ru.hse.mmstr_project.se.storage.fast_storage.repository.RedisItemRepository;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FastSchedulerManager {

    public static final int DUPLICATES_ALIVE_SECONDS = 180;

    private final RedisItemRepository repository;
    private final FastSchedulersMetrics fastSchedulersMetrics;
    private final SenderService senderService;

    public FastSchedulerManager(
            RedisItemRepository repository,
            FastSchedulersMetrics fastSchedulersMetrics,
            SenderService senderService) {
        this.repository = repository;
        this.fastSchedulersMetrics = fastSchedulersMetrics;
        this.senderService = senderService;
    }

    public void handle(Collection<IncidentMetadataDto> incidentMetadataDtos) {
        fastSchedulersMetrics.measureRequest(() -> handleI(incidentMetadataDtos));
    }

    private void handleI(Collection<IncidentMetadataDto> incidentMetadataDtos) {
        List<IncidentMetadataDto> sendToUsers = filterDuplicatesAndAlreadyConfirmed(incidentMetadataDtos);
        senderService.send(sendToUsers, true);
        repository.removeAll(incidentMetadataDtos.stream().toList());
    }

    private List<IncidentMetadataDto> filterDuplicatesAndAlreadyConfirmed(Collection<IncidentMetadataDto> incidentMetadataDtos) {
        Map<String, IncidentMetadataDto> collect = incidentMetadataDtos.stream()
                .collect(Collectors.toMap(IncidentMetadataDto::id, Function.identity(), (f, s) -> s));

        List<String> result = repository.filterDuplicates(collect.keySet())
                .stream()
                .toList();
        setProcessedLikeDuplicates(result);
        fastSchedulersMetrics.incDuplicatesFiltered(collect.keySet().size() - result.size());

        Set<String> alreadyConfirmed = new HashSet<>(repository.getConfirmKeys(result));
        result = result.stream().filter(it -> !alreadyConfirmed.contains(it)).toList();

        return result.stream().map(collect::get).toList();
    }

    private void setProcessedLikeDuplicates(Collection<String> incidentMetadataDtos) {
        repository.addToDeduplicationSet(incidentMetadataDtos, DUPLICATES_ALIVE_SECONDS);
    }
}
