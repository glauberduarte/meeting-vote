package com.glauber.voting.application.usecase;

import com.glauber.voting.application.boundary.SessionBoundary;
import com.glauber.voting.application.dto.AgendaDto;
import com.glauber.voting.application.dto.SessionDto;
import com.glauber.voting.domain.model.Session;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionUseCase {

    private final SessionBoundary agendaBoundary;

    public SessionUseCase(SessionBoundary agendaBoundary) {
        this.agendaBoundary = agendaBoundary;
    }

    public SessionDto.Response execute(SessionDto.Request request) {
        // 1. Converte o DTO de entrada para uma Entidade de Domínio
        Session session = new Session(null, request.getTitle(), SessionDto.toDomain(request.getAgendas()), request.getDurationInMinutes());

        // 2. Executa possíveis regras de negócio do caso de uso

        // 3. Salva através do Boundary (Abstração da infraestrutura)
        Session savedSession = agendaBoundary.save(session);

        // 4. Retorna o DTO de resposta esperado pela camada de entrega
        return SessionDto.Response.builder()
                .id(savedSession.getId())
                .title(savedSession.getTitle())
                .openingTime(savedSession.getOpeningTime())
                .closingTime(savedSession.getClosingTime())
                .build();
    }

    public SessionDto.AgendasResponse getAgendas(Long sessionId) {
        Session session = agendaBoundary.findById(sessionId);

        List<AgendaDto.Response> agendas = session.getAgendas().stream()
                .map(a -> AgendaDto.Response.builder().id(a.getId()).title(a.getTitle()).build())
                .collect(Collectors.toList());

        return SessionDto.AgendasResponse.builder()
                .agendas(agendas)
                .build();
    }
}