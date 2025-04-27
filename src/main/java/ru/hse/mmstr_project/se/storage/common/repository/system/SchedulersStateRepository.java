package ru.hse.mmstr_project.se.storage.common.repository.system;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.hse.mmstr_project.se.storage.common.entity.system.SchedulersState;

@Repository
public interface SchedulersStateRepository extends JpaRepository<SchedulersState, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE SchedulersState s SET s.successLastTry = false WHERE s.id = :id")
    int setLastTryFailedById(@Param("id") Long id);
}