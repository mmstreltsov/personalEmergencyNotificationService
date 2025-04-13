package ru.hse.mmstr_project.se.storage.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hse.mmstr_project.se.storage.common.entity.Scenario;

@Repository
public interface ScenarioRepository extends JpaRepository<Scenario, Long> {
}