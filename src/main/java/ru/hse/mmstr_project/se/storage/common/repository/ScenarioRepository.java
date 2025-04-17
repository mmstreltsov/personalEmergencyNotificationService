package ru.hse.mmstr_project.se.storage.common.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.hse.mmstr_project.se.storage.common.entity.Scenario;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Repository
public interface ScenarioRepository extends JpaRepository<Scenario, Long> {

    @Query("SELECT s FROM Scenario s WHERE s.firstTimeToActivate >= :startTime AND s.firstTimeToActivate <= :endTime ORDER BY s.firstTimeToActivate")
    Stream<Scenario> streamScenariosInTimeRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}