package ru.hse.mmstr_project.se.storage.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hse.mmstr_project.se.storage.common.entity.Client;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findAllByChatIdIn(Collection<Long> chatIds);
    Optional<Client> findByChatId(Long chatId);
}