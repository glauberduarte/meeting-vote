package com.glauber.voting.application.boundary;

import com.glauber.voting.domain.model.Session;
import reactor.core.publisher.Mono;

/**
 * Garante a utilização do cache e da fila de votação sem expor a infraestrutura.
 */
public interface VoteReactiveBoundary {
    Mono<Boolean> hasVoted(Long agendaId, Long sessionId, String cpf);
    void queueVote(Long agendaId, Long sessionId, String cpf, String choice);
    // Encapsula a busca e faz cache da assembleia
    Mono<Session> getSession(Long sessionId);
    Mono<Void> validateCpf(String cpf);
}