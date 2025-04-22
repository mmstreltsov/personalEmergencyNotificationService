package ru.hse.mmstr_project.se.service;

import org.springframework.stereotype.Service;
import ru.hse.mmstr_project.se.storage.fast_storage.dto.IncidentMetadataDto;
import ru.hse.mmstr_project.se.storage.fast_storage.repository.RedisItemRepository;

import java.util.Collection;

@Service
public class FastSchedulerManager {

    private final RedisItemRepository repository;

    public FastSchedulerManager(RedisItemRepository repository) {
        this.repository = repository;
    }

    public void handle(Collection<IncidentMetadataDto> incidentMetadataDtos) {
        System.out.println("HANDLED " + incidentMetadataDtos.size());
        repository.removeAll(incidentMetadataDtos.stream().limit(incidentMetadataDtos.size() / 2).toList());
    }
}
