package ru.hse.mmstr_project.se.storage.common.entity.system;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "schedulers")
public class SchedulersState {

    @Id
    private String id;

    @Column(name = "fetchTime")
    private Long fetchTime;

    public SchedulersState() {
    }

    public SchedulersState(String id, Long fetchTime) {
        this.id = id;
        this.fetchTime = fetchTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getFetchTime() {
        return fetchTime;
    }

    public void setFetchTime(Long fetchTime) {
        this.fetchTime = fetchTime;
    }
}
