package com.glauber.voting.infrastructure.adapter;

import com.glauber.voting.application.boundary.SessionBoundary;
import com.glauber.voting.application.boundary.VoteReactiveBoundary;
import com.glauber.voting.domain.model.Session;
import com.glauber.voting.infrastructure.client.CpfValidator;
import com.glauber.voting.infrastructure.config.VoteEvent;
import com.glauber.voting.infrastructure.exception.CpfValidatorException;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

@Component
@Transactional
public class VoteReactiveAdapter implements VoteReactiveBoundary {
    private final CacheManager cacheManager;
    private final Sinks.Many<VoteEvent> voteSink;
    private final SessionBoundary sessionBoundary;
    private final CpfValidator cpfValidator;

    public VoteReactiveAdapter(CacheManager cacheManager, Sinks.Many<VoteEvent> voteSink, SessionBoundary sessionBoundary, CpfValidator cpfValidator) {
        this.cacheManager = cacheManager;
        this.voteSink = voteSink;
        this.sessionBoundary = sessionBoundary;
        this.cpfValidator = cpfValidator;
    }

    @Override
    public Mono<Boolean> hasVoted(Long agendaId, Long sessionId, String cpf) {
        return Mono.fromSupplier(() -> {
            String cacheKey = String.format("agenda:%d:session:%d:affiliated:%s", agendaId, sessionId, cpf);
            Cache cache = cacheManager.getCache("votedCPFs");

            if (cache != null && cache.get(cacheKey) != null) {
                return true; // Já votou
            }

            // se não votou, salva no cache para evitar concorrência simultânea
            if (cache != null) {
                cache.put(cacheKey, true);
            }
            return false;
        });
    }

    @Override
    public void queueVote(Long agendaId, Long sessionId, String cpf, String choice) {
        VoteEvent event = new VoteEvent(agendaId, sessionId, cpf, choice);

        // Publica no Sink reativo, VoteBatchRepository irá consumir e processar em batch
        voteSink.tryEmitNext(event);
    }

    @Override
    public Mono<Session> getSession(Long sessionId) {
        return Mono.defer(() -> {
            Cache cache = cacheManager.getCache("sessions");

            if (cache != null) {
                Session cachedSession = cache.get(sessionId, Session.class);
                if (cachedSession != null) {
                    // Retorna imediatamente sem abrir Threads extras
                    return Mono.just(cachedSession);
                }
            }

            // Se não esta no cache, isola a chamada síncrona do JPA no Pool de Threads para não bloquear o reactor
            return Mono.fromCallable(() -> sessionBoundary.findById(sessionId))
                    .subscribeOn(Schedulers.boundedElastic())
                    .doOnNext(session -> {
                        if (cache != null && session != null) {
                            cache.put(sessionId, session);
                        }
                    });
        });
    }

    @Override
    public Mono<Void> validateCpf(String cpf) {
        return Mono.<Void>fromRunnable(() -> {
                    try {
                        cpfValidator.isValid(cpf);
                    } catch (CpfValidatorException ex) {
                        throw ex;
                    } catch (Exception ex) {
                        throw new CpfValidatorException("Falha na comunicação com o validador de CPF."+ ex.getMessage());
                    }
                })
                // Desvia essa execução bloqueante para Threads secundárias do Spring,
                // mantendo as Threads principais do Netty livres e responsivas.
                .subscribeOn(Schedulers.boundedElastic());
    }
}