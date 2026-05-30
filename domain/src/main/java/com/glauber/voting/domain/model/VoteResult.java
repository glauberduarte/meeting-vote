package com.glauber.voting.domain.model;

import lombok.Getter;

public class VoteResult {
    @Getter
    private final Long agendaId;
    @Getter
    private final Long yesVotes;
    @Getter
    private final Long noVotes;
    @Getter
    private final Long totalVotes;

    public VoteResult(Long agendaId, Long yesVotes, Long noVotes) {
        this.agendaId = agendaId;
        this.yesVotes = yesVotes;
        this.noVotes = noVotes;
        this.totalVotes = (yesVotes != null ? yesVotes : 0L) + (noVotes != null ? noVotes : 0L);
    }
}

