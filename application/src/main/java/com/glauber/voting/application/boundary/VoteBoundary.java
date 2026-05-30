package com.glauber.voting.application.boundary;

import com.glauber.voting.domain.model.Vote;
import com.glauber.voting.domain.model.VoteResult;

import java.util.List;

public interface VoteBoundary {
    Vote save(Vote vote);

    List<VoteResult> countResultsBySession(Long sessionId);
}

