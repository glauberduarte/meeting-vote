package com.glauber.voting.domain.model;

public class Agenda {
    private Long id;
    private String title;

    public Agenda(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}