package ru.hse.mmstr_project.se.service.storage;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.hse.mmstr_project.se.storage.common.dto.CreateScenarioDto;
import ru.hse.mmstr_project.se.storage.common.dto.ScenarioDto;
import ru.hse.mmstr_project.se.storage.common.mapper.ClientMapper;
import ru.hse.mmstr_project.se.storage.common.repository.BatchPaginationIterator;
import ru.hse.mmstr_project.se.storage.common.repository.ScenarioRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ScenarioStorage {

    private final ScenarioRepository scenarioRepository;
    private final ClientMapper clientMapper;

    public ScenarioStorage(ScenarioRepository scenarioRepository, ClientMapper clientMapper) {
        this.scenarioRepository = scenarioRepository;
        this.clientMapper = clientMapper;
    }

    @Transactional
    public void saveAll(Collection<ScenarioDto> scenarioDtos) {
        scenarioRepository.saveAll(scenarioDtos.stream().map(clientMapper::toEntity).toList());
    }

    @Transactional
    public void save(CreateScenarioDto scenarioDto) {
        scenarioRepository.save(clientMapper.toEntity(scenarioDto));
    }

    public Iterator<List<ScenarioDto>> iterateScenariosInBatches(
            Instant from, Instant to, int batchSize, AtomicBoolean cancelled) {

        return new BatchPaginationIterator<>(
                pageable -> scenarioRepository.findScenariosInTimeRange(from, to, pageable),
                clientMapper::toDto,
                batchSize,
                cancelled
        );
    }
}
