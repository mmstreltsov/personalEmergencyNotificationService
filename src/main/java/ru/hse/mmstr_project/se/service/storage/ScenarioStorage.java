package ru.hse.mmstr_project.se.service.storage;

import org.springframework.stereotype.Service;
import ru.hse.mmstr_project.se.storage.common.dto.CreateScenarioDto;
import ru.hse.mmstr_project.se.storage.common.dto.ScenarioDto;
import ru.hse.mmstr_project.se.storage.common.mapper.ClientMapper;
import ru.hse.mmstr_project.se.storage.common.repository.ScenarioRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.stream.Stream;

@Service
public class ScenarioStorage {

    private final ScenarioRepository scenarioRepository;
    private final ClientMapper clientMapper;

    public ScenarioStorage(ScenarioRepository scenarioRepository, ClientMapper clientMapper) {
        this.scenarioRepository = scenarioRepository;
        this.clientMapper = clientMapper;
    }

    public void saveAll(Collection<ScenarioDto> scenarioDtos) {
        scenarioRepository.saveAll(scenarioDtos.stream().map(clientMapper::toEntity).toList());
    }

    public void save(CreateScenarioDto scenarioDto) {
        scenarioRepository.save(clientMapper.toEntity(scenarioDto));
    }

    public Stream<ScenarioDto> streamScenariosInTimeRange(Instant startTime, Instant endTime) {
        return scenarioRepository.streamScenariosInTimeRange(startTime, endTime).map(clientMapper::toDto);
    }
}
