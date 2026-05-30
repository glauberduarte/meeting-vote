package com.glauber.voting.domain.model;

import lombok.Getter;

public class Vote {
    @Getter
    private final Long id;
    @Getter
    private final Long sessionId;
    @Getter
    private final Long agendaId;
    @Getter
    private final String affiliatedId;
    @Getter
    private final VoteChoice choice;

    public Vote(Long id, Long sessionId, Long agendaId, String affiliatedId, VoteChoice choice) {
        this.id = id;
        this.sessionId = sessionId;
        this.agendaId = agendaId;
        this.affiliatedId = affiliatedId;
        this.choice = choice;
    }
}

