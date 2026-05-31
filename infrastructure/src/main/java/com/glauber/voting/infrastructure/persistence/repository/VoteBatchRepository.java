package com.glauber.voting.infrastructure.persistence.repository;

import com.glauber.voting.infrastructure.config.VoteEvent;
import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;

@Repository
public class VoteBatchRepository {

    private final JdbcTemplate jdbcTemplate;
    private final Sinks.Many<VoteEvent> voteSink;

    public static final String INSERT_INTO_VOTES =
            "INSERT INTO votes (agenda_id, affiliated_id, session_id, choice, created_at) " +
                    "VALUES (?, ?, ?, ?, now())" +
                    "ON CONFLICT (agenda_id, affiliated_id, session_id) " +
                    "DO NOTHING";

    public VoteBatchRepository(JdbcTemplate jdbcTemplate, Sinks.Many<VoteEvent> voteSink) {
        this.jdbcTemplate = jdbcTemplate;
        this.voteSink = voteSink;
    }

    @PostConstruct
    public void init() {
        voteSink.asFlux()
                .bufferTimeout(1000, Duration.ofSeconds(1)) // Agrupa 1000 votos ou a cada 1 segundo
                .publishOn(Schedulers.boundedElastic()) // Cria um pool de threads, da execução síncrona do JDBC
                .subscribe(this::executeBatchInsert);
    }

    private void executeBatchInsert(List<VoteEvent> events) {
        if (events.isEmpty())
            return;

        jdbcTemplate.batchUpdate(INSERT_INTO_VOTES, events, events.size(), (ps, event) -> {
            ps.setLong(1, event.agendaId());
            ps.setString(2, event.affiliatedId());
            ps.setLong(3, event.sessionId());
            ps.setString(4, event.choice());
        });
    }
}