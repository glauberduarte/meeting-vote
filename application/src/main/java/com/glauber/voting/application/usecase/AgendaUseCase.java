package com.glauber.voting.application.usecase;

import com.glauber.voting.application.dto.AgendaDto;
import com.glauber.voting.application.boundary.AgendaBoundary;
import com.glauber.voting.domain.model.Agenda;
import org.springframework.stereotype.Service;

@Service
public class AgendaUseCase {

    private final AgendaBoundary agendaBoundary;

    public AgendaUseCase(AgendaBoundary agendaBoundary) {
        this.agendaBoundary = agendaBoundary;
    }

    public AgendaDto.Response execute(AgendaDto.Request request) {
        // 1. Converte o DTO de entrada para uma Entidade de Domínio
        Agenda agenda = new Agenda(null, request.getTitle());

        // 2. Executa possíveis regras de negócio do caso de uso

        // 3. Salva através do Boundary (Abstração da infraestrutura)
        Agenda savedAgenda = agendaBoundary.save(agenda);

        // 4. Retorna o DTO de resposta esperado pela camada de entrega
        return AgendaDto.Response.builder()
                .id(savedAgenda.getId())
                .title(savedAgenda.getTitle())
                .build();
    }
}