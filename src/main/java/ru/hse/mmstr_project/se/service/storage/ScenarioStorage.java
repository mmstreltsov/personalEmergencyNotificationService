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
import java.util.UUID;
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
    public void saveAllCreatingDto(Collection<CreateScenarioDto> scenarioDtos) {
        scenarioRepository.saveAll(scenarioDtos.stream().map(clientMapper::toEntity).toList());
    }

    @Transactional
    public String save(CreateScenarioDto scenarioDto) {
        return scenarioRepository.save(clientMapper.toEntity(scenarioDto)).getUuid().toString();
    }

    @Transactional
    public void deleteByIds(List<Long> ids) {
        scenarioRepository.deleteAllByIdInBatch(ids);
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

    public List<ScenarioDto> findAllByClientIdAndName(long id, String name) {
        return scenarioRepository.findAllByClientIdAndNameContaining(id, name).stream().map(clientMapper::toDto).toList();
    }

    public List<ScenarioDto> findAllByClientId(long id) {
        return scenarioRepository.findAllByClientId(id).stream().map(clientMapper::toDto).toList();
    }

    public List<ScenarioDto> findAllByUuid(UUID uuid) {
        return scenarioRepository.findAllByUuid(uuid).stream().map(clientMapper::toDto).toList();
    }
}
