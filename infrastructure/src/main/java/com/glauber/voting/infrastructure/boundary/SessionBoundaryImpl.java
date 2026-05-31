package com.glauber.voting.infrastructure.boundary;

import com.glauber.voting.application.boundary.SessionBoundary;
import com.glauber.voting.application.exception.SessionException;
import com.glauber.voting.domain.model.Agenda;
import com.glauber.voting.domain.model.Session;
import com.glauber.voting.infrastructure.persistence.entity.AgendaEntity;
import com.glauber.voting.infrastructure.persistence.entity.SessionEntity;
import com.glauber.voting.infrastructure.persistence.repository.AgendaRepository;
import com.glauber.voting.infrastructure.persistence.repository.SessionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Transactional
public class SessionBoundaryImpl implements SessionBoundary {

    private final SessionRepository repository;
    private final AgendaRepository agendaRepository;

    public SessionBoundaryImpl(SessionRepository repository, AgendaRepository agendaRepository) {
        this.repository = repository;
        this.agendaRepository = agendaRepository;
    }

    @Override
    public Session save(Session session) {
        List<AgendaEntity> agendaEntities = session.getAgendas().stream()
                .map(a -> agendaRepository.getReferenceById(a.getId()))
                .collect(Collectors.toList());

        // Mapeia Domínio -> Entidade JPA
        SessionEntity entityToSave = SessionEntity.builder()
                .agendas(agendaEntities)
                .assemblyTitle(session.getTitle())
                .durationInMinutes(session.getDurationInMinutes())
                .openingTime(session.getOpeningTime())
                .closingTime(session.getClosingTime())
                .build();

        // Persiste de fato utilizando o Spring JPA
        SessionEntity savedEntity = repository.save(entityToSave);

        // Mapeia Entidade JPA -> Domínio
        return new Session(savedEntity.getId(), savedEntity.getAssemblyTitle(), session.getAgendas(), savedEntity.getDurationInMinutes(), savedEntity.getOpeningTime(), savedEntity.getClosingTime());
    }

    @Override
    public Session findById(Long id) {
        Objects.requireNonNull(id, "id não pode ser nulo.");
        SessionEntity entity = repository.findById(id)
                .orElseThrow(() -> new SessionException("Session not found: " + id));

        List<Agenda> agendas = entity.getAgendas().stream()
                .map(a -> new Agenda(a.getId(), a.getTitle()))
                .collect(Collectors.toList());

        return new Session(entity.getId(), entity.getAssemblyTitle(), agendas, entity.getDurationInMinutes(), entity.getOpeningTime(), entity.getClosingTime());
    }
}