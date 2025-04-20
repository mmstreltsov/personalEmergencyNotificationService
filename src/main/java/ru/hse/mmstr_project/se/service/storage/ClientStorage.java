package ru.hse.mmstr_project.se.service.storage;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.hse.mmstr_project.se.storage.common.dto.ClientDto;
import ru.hse.mmstr_project.se.storage.common.dto.CreateClientDto;
import ru.hse.mmstr_project.se.storage.common.mapper.ClientMapper;
import ru.hse.mmstr_project.se.storage.common.repository.ClientRepository;

import java.util.Collection;
import java.util.List;

@Service
public class ClientStorage {
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public ClientStorage(ClientRepository clientRepository, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    @Transactional
    public void save(CreateClientDto scenarioDto) {
        clientRepository.save(clientMapper.toEntity(scenarioDto));
    }

    @Transactional
    public List<ClientDto> findAllByIds(Collection<Long> ids) {
        return clientRepository.findAllById(ids).stream().map(clientMapper::toDto).toList();
    }
}
