package com.glauber.voting.application.usecase;

import com.glauber.voting.application.boundary.SessionBoundary;
import com.glauber.voting.application.boundary.VoteReactiveBoundary;
import com.glauber.voting.application.dto.VoteDto;
import com.glauber.voting.application.exception.SessionException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class VoteReactiveUseCase {
    private final VoteReactiveBoundary voteReactiveBoundary;

    public VoteReactiveUseCase(VoteReactiveBoundary voteReactiveBoundary) {
        this.voteReactiveBoundary = voteReactiveBoundary;
    }

    public Mono<VoteDto.Response> execute(Long sessionId, VoteDto.Request request) {
        Long agendaId = request.getAgendaId();
        String cpf = request.getCpf();

        // Validação de duplicidade no cache de CPFs
        return voteReactiveBoundary.hasVoted(agendaId, sessionId, cpf)
                .flatMap(alreadyVoted -> {
                    if (alreadyVoted) {
                        return Mono.error(new SessionException("vote.cpf_already_voted", "Este associado já votou nesta pauta dentro desta sessão.", cpf));
                    }

                    // Busca sessão do cache
                    return voteReactiveBoundary.getSession(sessionId)
                            .switchIfEmpty(Mono.error(new SessionException("session.not_found", "Sessão não encontrada.", sessionId)));
                })
                .flatMap(session -> {
                    // Verifica se ainda é possível realizar votação
                    if (!session.isOpen()) {
                        return Mono.error(new SessionException("session.already_closed", "Assembléia finalizada, não pode mais receber votos.", session.getClosingTime()));
                    }

                    // Se a sessão está aberta, dispara a validação reativa do CPF
                    // O '.then(Mono.just(session))' garante que se o CPF for válido o fluxo irá continuar
                    return voteReactiveBoundary.validateCpf(cpf)
                            .then(Mono.just(session));
                })
                .flatMap(session -> {
                    // Envia o voto para processamento em lote
                    voteReactiveBoundary.queueVote(agendaId, sessionId, cpf, request.getChoice());
                    return Mono.just(VoteDto.Response.builder()
                            .agendaId(agendaId)
                            .cpf(cpf)
                            .sessionId(sessionId)
                            .choice(request.getChoice())
                            .build());
                });
    }
}