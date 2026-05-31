package com.glauber.voting.infrastructure.boundary;

import com.glauber.voting.application.boundary.AgendaBoundary;
import com.glauber.voting.domain.model.Agenda;
import com.glauber.voting.infrastructure.persistence.entity.AgendaEntity;
import com.glauber.voting.infrastructure.persistence.repository.AgendaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Component
@Transactional
public class AgendaBoundaryImpl implements AgendaBoundary {

    private final AgendaRepository repository;

    public AgendaBoundaryImpl(AgendaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Agenda save(Agenda agenda) {
        // Validações básicas
        Objects.requireNonNull(agenda, "agenda não pode ser nulo.");
        if (agenda.getTitle() == null || agenda.getTitle().isBlank()) {
            throw new IllegalArgumentException("Agenda title não pode ser vazio.");
        }

        // Mapeia Domínio -> Entidade JPA
        AgendaEntity entityToSave = AgendaEntity.builder()
                .title(agenda.getTitle())
                .build();

        // Persiste de fato utilizando o Spring JPA
        AgendaEntity savedEntity = repository.save(entityToSave);

        // Mapeia Entidade JPA -> Domínio
        return new Agenda(savedEntity.getId(), savedEntity.getTitle());
    }
}