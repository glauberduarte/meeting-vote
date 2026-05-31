package com.glauber.voting.infrastructure.boundary;

import com.glauber.voting.application.boundary.VoteBoundary;
import com.glauber.voting.application.exception.SessionException;
import com.glauber.voting.domain.model.Vote;
import com.glauber.voting.domain.model.VoteResult;
import com.glauber.voting.infrastructure.client.CpfValidator;
import com.glauber.voting.infrastructure.exception.CpfValidatorException;
import com.glauber.voting.infrastructure.persistence.entity.VoteEntity;
import com.glauber.voting.infrastructure.persistence.repository.VoteRepository;
import com.glauber.voting.application.boundary.SessionBoundary;
import com.glauber.voting.domain.model.Session;
import com.glauber.voting.domain.model.Agenda;
import com.glauber.voting.domain.model.VoteChoice;

import java.io.IOException;
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
    private final CpfValidator cpfValidator;

    public VoteBoundaryImpl(VoteRepository repository, SessionBoundary sessionBoundary, CpfValidator cpfValidator) {
        this.repository = repository;
        this.sessionBoundary = sessionBoundary;
        this.cpfValidator = cpfValidator;
    }

    @Override
    public Vote save(Vote vote) {
        // Busca em componente externo de validação de CPF, por esse motivo está na infrastructure
        validateCPF(vote.getAffiliatedId());

        boolean affiliateAlreadyVoted = repository.existsByAffiliatedIdAndSessionIdAndAgendaId(vote.getAffiliatedId(), vote.getSessionId(), vote.getAgendaId());
        if (affiliateAlreadyVoted) {
            throw new CpfValidatorException("Associado já votou nessa pauta e assembleia.");
        }

        Session session = sessionBoundary.findById(vote.getSessionId());
        if (!session.isOpen()) {
            throw new SessionException("Assembléia finalizada, não pode mais receber votos.");
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
        Objects.requireNonNull(sessionId, "sessionId não pode ser nulo.");

        Session session = sessionBoundary.findById(sessionId);
        // Se a sessão ainda não foi finalizada, lança exceção para sinalizar warning
        if (session.isOpen()) {
            throw new SessionException("Sessão ainda não finalizada");
        }

        // Buscar todos os votos das agendas da sessão
        List<Long> agendaIds = session.getAgendas().stream()
                .map(Agenda::getId)
                .collect(Collectors.toList());

        List<VoteEntity> votes = repository.findBySessionIdAndAgendaIdIn(sessionId, agendaIds);

        // Sumarizar sim e nao por agenda, e garantir que retornamos todas as agendas da sessão
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

    private void validateCPF(String cpf) {
        try {
            cpfValidator.isValid(cpf);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("CPF inválido.");
        }
    }
}