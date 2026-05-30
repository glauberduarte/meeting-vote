package com.glauber.voting.infrastructure.boundary;

import com.glauber.voting.application.boundary.VoteBoundary;
import com.glauber.voting.application.exception.SessionException;
import com.glauber.voting.domain.model.Vote;
import com.glauber.voting.domain.model.VoteResult;
import com.glauber.voting.infrastructure.persistence.entity.VoteEntity;
import com.glauber.voting.infrastructure.persistence.repository.VoteRepository;
import com.glauber.voting.application.boundary.SessionBoundary;
import com.glauber.voting.domain.model.Session;
import com.glauber.voting.domain.model.Agenda;
import com.glauber.voting.domain.model.VoteChoice;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Component
@Transactional
public class VoteBoundaryImpl implements VoteBoundary {

    private final VoteRepository repository;
    private final SessionBoundary sessionBoundary;

    public VoteBoundaryImpl(VoteRepository repository, SessionBoundary sessionBoundary) {
        this.repository = repository;
        this.sessionBoundary = sessionBoundary;
    }

    @Override
    public Vote save(Vote vote) {
        // Validações básicas
        Objects.requireNonNull(vote, "vote must not be null");
        if (vote.getAgendaId() == null) {
            throw new IllegalArgumentException("Vote agendaId must not be blank");
        }

        if (vote.getSessionId() == null) {
            throw new IllegalArgumentException("Vote sessionId must not be blank");
        }

        if (vote.getAffiliatedId() == null) {
            throw new IllegalArgumentException("Vote affiliatedId must not be blank");
        }

        // Mapeia Domínio -> Entidade JPA
        VoteEntity entityToSave = VoteEntity.builder()
                .agendaId(vote.getAgendaId())
                .affiliatedId(vote.getAffiliatedId())
                .sessionId(vote.getSessionId())
                .choice(vote.getChoice().toString())
                .build();

        // Persiste de fato utilizando o Spring JPA
        VoteEntity savedEntity = repository.save(entityToSave);

        // Mapeia Entidade JPA -> Domínio
        return new Vote(savedEntity.getId(), savedEntity.getSessionId(), savedEntity.getAgendaId(), savedEntity.getAffiliatedId(), vote.getChoice());
    }

    @Override
    public List<VoteResult> countResultsBySession(Long sessionId) {
        Objects.requireNonNull(sessionId, "sessionId must not be null");

        // 1 - Recupera a sessão e valida se está finalizada (considerando UTC-3)
        Session session = sessionBoundary.findById(sessionId);
        LocalDateTime now = ZonedDateTime.now(ZoneOffset.ofHours(-3)).toLocalDateTime();

        // Se a sessão ainda não foi finalizada, lança exceção para sinalizar warning
        if (session.getClosingTime() == null || session.getClosingTime().isAfter(now)) {
            throw new SessionException("Sessão ainda não finalizada");
        }

        // 2 - Buscar todos os votos das agendas da sessão
        java.util.List<Long> agendaIds = session.getAgendas().stream()
                .map(Agenda::getId)
                .collect(Collectors.toList());

        java.util.List<VoteEntity> votes = repository.findBySessionIdAndAgendaIdIn(sessionId, agendaIds);

        // 3 - Sumarizar sim e nao por agenda, e garantir que retornamos todas as agendas da sessão
        Map<Long, VoteResult> accumulator = new HashMap<>();
        for (Long agendaId : agendaIds) {
            accumulator.put(agendaId, new VoteResult(agendaId, 0L, 0L));
        }

        for (VoteEntity v : votes) {
            Long aid = v.getAgendaId();
            VoteChoice choice;
            try {
                choice = VoteChoice.fromString(v.getChoice());
            } catch (IllegalArgumentException ex) {
                // ignorar votos inválidos
                continue;
            }

            VoteResult current = accumulator.getOrDefault(aid, new VoteResult(aid, 0L, 0L));
            if (choice == VoteChoice.SIM) {
                current = new VoteResult(aid, current.getYesVotes() + 1, current.getNoVotes());
            } else {
                current = new VoteResult(aid, current.getYesVotes(), current.getNoVotes() + 1);
            }
            accumulator.put(aid, current);
        }

        return accumulator.values().stream().collect(Collectors.toList());
    }


}