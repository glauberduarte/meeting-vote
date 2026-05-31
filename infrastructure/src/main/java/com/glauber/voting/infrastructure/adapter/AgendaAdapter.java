package com.glauber.voting.infrastructure.adapter;

import com.glauber.voting.application.boundary.AgendaBoundary;
import com.glauber.voting.domain.model.Agenda;
import com.glauber.voting.infrastructure.persistence.entity.AgendaEntity;
import com.glauber.voting.infrastructure.persistence.repository.AgendaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Component
@Transactional
public class AgendaAdapter implements AgendaBoundary {

    private final AgendaRepository repository;

    public AgendaAdapter(AgendaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Agenda save(Agenda agenda) {
        AgendaEntity entityToSave = AgendaEntity.builder()
                .title(agenda.getTitle())
                .build();

        AgendaEntity savedEntity = repository.save(entityToSave);
        return new Agenda(savedEntity.getId(), savedEntity.getTitle());
    }
}