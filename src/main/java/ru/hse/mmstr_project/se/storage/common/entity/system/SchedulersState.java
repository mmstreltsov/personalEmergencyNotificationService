package ru.hse.mmstr_project.se.storage.common.entity.system;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "schedulers")
public class SchedulersState {

    @Id
    private Long id;

    @Column(name = "fetchTime")
    private Instant fetchTime;

    @Column(name = "successLastTry")
    private Boolean successLastTry;

    public SchedulersState() {
    }

    public SchedulersState(Long id, Instant fetchTime) {
        this(id, fetchTime, Boolean.TRUE);
    }

    public SchedulersState(Long id, Instant fetchTime, Boolean successLastTry) {
        this.id = id;
        this.fetchTime = fetchTime;
        this.successLastTry = successLastTry;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getFetchTime() {
        return fetchTime;
    }

    public void setFetchTime(Instant fetchTime) {
        this.fetchTime = fetchTime;
    }

    public Boolean isSuccessLastTry() {
        return successLastTry;
    }
}
