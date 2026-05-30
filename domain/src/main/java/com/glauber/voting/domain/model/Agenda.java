package com.glauber.voting.domain.model;

import lombok.Getter;

public class Agenda {
    @Getter
    private Long id;
    @Getter
    private String title;

    public Agenda(Long id, String title) {
        this.id = id;
        this.title = title;
    }

}