package ru.hse.mmstr_project.se.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.hse.mmstr_project.se.storage.common.dto.ClientDto;
import ru.hse.mmstr_project.se.storage.common.dto.CreateClientDto;
import ru.hse.mmstr_project.se.storage.common.dto.CreateScenarioDto;
import ru.hse.mmstr_project.se.storage.common.dto.FriendDto;
import ru.hse.mmstr_project.se.storage.common.mapper.ClientMapper;
import ru.hse.mmstr_project.se.storage.common.repository.ClientRepository;
import ru.hse.mmstr_project.se.storage.common.repository.ScenarioRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@Transactional
public class A {

    private final ClientRepository clientRepository;
    private final ScenarioRepository scenarioRepository;
    private final ClientMapper clientMapper;

    public A(ClientRepository clientRepository, ScenarioRepository scenarioRepository,
             ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.scenarioRepository = scenarioRepository;
        this.clientMapper = clientMapper;
    }

    public void ahahahah() {

        CreateClientDto clientDto = new CreateClientDto(new Random().nextLong(), 123L, List.of(FriendDto.builder()
                .id(1)
                .name("ahahah")
                .chatId(1)
                .wayToNotify(List.of("tg", "ahahahhaha"))
                .phoneNumber("+1")
                .build()));

        clientRepository.save(clientMapper.toEntity(clientDto));
        System.out.println("DONE");
        System.out.println(clientRepository.findAll().stream().map(clientMapper::toDto).map(ClientDto::getListOfFriends).toList());
    }

    public void oohohoohohoh() {

        CreateScenarioDto ahahhahaha = new CreateScenarioDto(
                "",
                1L,
                List.of(),
                LocalDateTime.now(),
                List.of(LocalDateTime.now().plusDays(1)),
                1,
                false,
                "ahahhahaha");

        scenarioRepository.save(clientMapper.toEntity(ahahhahaha));

        System.out.println(scenarioRepository.findAll().stream().map(clientMapper::toDto).toList());
    }
}
