package ru.hse.mmstr_project.se.storage.common.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.hse.mmstr_project.se.storage.common.entity.Scenario;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScenarioRepository extends JpaRepository<Scenario, Long> {

    @Query("SELECT s FROM Scenario s WHERE s.firstTimeToActivate >= :startTime AND s.firstTimeToActivate < :endTime AND s.firstTimeToActivate < cast('infinity' as timestamp) ORDER BY s.firstTimeToActivate ASC")
    Page<Scenario> findScenariosInTimeRange(
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime,
            Pageable pageable);

    Optional<Scenario> findFirstByClientIdAndFirstTimeToActivateLessThanOrderByFirstTimeToActivateDesc(Long clientId, Instant firstTimeToActivate);

    Optional<Scenario> findFirstByClientIdAndFirstTimeToActivateGreaterThanOrderByFirstTimeToActivateAsc(Long clientId, Instant firstTimeToActivate);

    List<Scenario> findAllByClientId(Long clientId);

    List<Scenario> findAllByClientIdAndNameContaining(Long clientId, String name);

    List<Scenario> findAllByUuid(UUID uuid);
}