package ru.hse.mmstr_project.se.service.storage;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.hse.mmstr_project.se.storage.common.dto.CreateIncidentDto;
import ru.hse.mmstr_project.se.storage.common.mapper.ClientMapper;
import ru.hse.mmstr_project.se.storage.common.repository.IncidentRepository;

import java.util.List;

@Service
public class IncidentStorage {
    private final IncidentRepository incidentRepository;
    private final ClientMapper clientMapper;

    public IncidentStorage(
            IncidentRepository incidentRepository,
            ClientMapper clientMapper) {
        this.incidentRepository = incidentRepository;
        this.clientMapper = clientMapper;
    }

    @Transactional
    public void saveAll(List<CreateIncidentDto> dtos) {
        incidentRepository.saveAll(dtos.stream().map(clientMapper::toEntity).toList());
    }
}
