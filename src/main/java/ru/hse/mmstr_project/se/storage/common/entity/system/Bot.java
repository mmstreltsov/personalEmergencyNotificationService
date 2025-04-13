package ru.hse.mmstr_project.se.storage.common.entity.system;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bots")
public class Bot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "countOfUsers")
    private Long countOfUsers;

    public Bot() {
    }

    public Bot(String name, String url, Long countOfUsers) {
        this.name = name;
        this.url = url;
        this.countOfUsers = countOfUsers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCountOfUsers() {
        return countOfUsers;
    }

    public void setCountOfUsers(Long countOfUsers) {
        this.countOfUsers = countOfUsers;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
