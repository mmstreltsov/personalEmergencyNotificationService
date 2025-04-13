package ru.hse.mmstr_project.se.storage.common.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.hse.mmstr_project.se.storage.common.dto.ClientDto;
import ru.hse.mmstr_project.se.storage.common.dto.CreateClientDto;
import ru.hse.mmstr_project.se.storage.common.dto.CreateScenarioDto;
import ru.hse.mmstr_project.se.storage.common.dto.FriendDto;
import ru.hse.mmstr_project.se.storage.common.dto.ScenarioDto;
import ru.hse.mmstr_project.se.storage.common.entity.Client;
import ru.hse.mmstr_project.se.storage.common.entity.Friend;
import ru.hse.mmstr_project.se.storage.common.entity.Scenario;

@Mapper
public interface ClientMapper {
    ClientDto toDto(Client client);

    Client toEntity(ClientDto clientDto);

    @Mapping(target = "id", ignore = true)
    Client toEntity(CreateClientDto clientDto);

    FriendDto toFriendDto(Friend friend);

    Friend toFriendEntity(FriendDto friendDto);

    ScenarioDto toDto(Scenario scenario);

    Scenario toEntity(ScenarioDto scenarioDto);

    @Mapping(target = "id", ignore = true)
    Scenario toEntity(CreateScenarioDto scenarioDto);
}