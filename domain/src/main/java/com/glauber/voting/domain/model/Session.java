package com.glauber.voting.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class Session {
    @Getter
    private final Long id;
    @Getter
    private final String title;
    @Getter
    private final Integer durationInMinutes;
    @Getter
    private final List<Agenda> agendas;
    @Getter
    private final LocalDateTime openingTime;
    @Getter
    private final LocalDateTime closingTime;

    public Session(Long id, String title, List<Agenda> agendas, Integer durationInMinutes) {
        this.id = id;
        this.title = title;
        this.agendas = agendas;
        this.durationInMinutes = durationInMinutes;
        this.openingTime = LocalDateTime.now();
        // Caso não seja informado o tempo, a sessão dura 1 minuto
        this.closingTime = this.openingTime.plusMinutes(durationInMinutes != null ? durationInMinutes : 1);
    }

    public boolean isOpen() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(openingTime) && now.isBefore(closingTime);
    }

}