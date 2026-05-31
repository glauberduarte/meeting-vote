package com.glauber.voting.application.usecase;

import com.glauber.voting.application.boundary.VoteBoundary;
import com.glauber.voting.application.dto.VoteDto;
import com.glauber.voting.domain.model.Vote;
import com.glauber.voting.domain.model.VoteChoice;
import com.glauber.voting.domain.model.VoteResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class VoteUseCase {

    private final VoteBoundary voteBoundary;

    public VoteUseCase(VoteBoundary voteBoundary) {
        this.voteBoundary = voteBoundary;
    }

    public VoteDto.Response execute(Long sessionId, VoteDto.Request request) {
        VoteChoice voteChoice = VoteChoice.fromString(request.getChoice());
        Vote vote = new Vote(null, sessionId, request.getAgendaId(), request.getCpf(), voteChoice);

        Vote saved = voteBoundary.save(vote);

        return VoteDto.Response.builder()
                .id(saved.getId())
                .sessionId(saved.getSessionId())
                .agendaId(saved.getAgendaId())
                .cpf(saved.getAffiliatedId())
                .choice(saved.getChoice() != null ? saved.getChoice().toString() : null)
                .build();
    }

    public VoteDto.ResultsResponse getResults(Long sessionId) {
        List<VoteResult> results = voteBoundary.countResultsBySession(sessionId);

        List<VoteDto.AgendaResult> dto = results.stream()
                .map(r -> VoteDto.AgendaResult.builder()
                        .agendaId(r.getAgendaId())
                        .yesVotes(r.getYesVotes())
                        .noVotes(r.getNoVotes())
                        .totalVotes(r.getTotalVotes())
                        .build())
                .collect(Collectors.toList());

        return VoteDto.ResultsResponse.builder().results(dto).build();
    }
}

