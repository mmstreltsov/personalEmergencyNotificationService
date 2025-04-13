package ru.hse.mmstr_project.se.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.hse.mmstr_project.se.storage.common.dto.ClientDto;
import ru.hse.mmstr_project.se.storage.common.dto.CreateClientDto;
import ru.hse.mmstr_project.se.storage.common.mapper.ClientMapper;
import ru.hse.mmstr_project.se.storage.common.repository.ClientRepository;

@Service
@Transactional
public class A {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public A(ClientRepository clientRepository,
            ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    public void ahahahah(CreateClientDto clientDto) {
        clientRepository.save(clientMapper.toEntity(clientDto));
        System.out.println("DONE");
        System.out.println(clientRepository.findAll().stream().map(clientMapper::toDto).map(ClientDto::getListOfFriends).toList());
    }
}
