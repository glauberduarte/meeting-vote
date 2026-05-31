package com.glauber.voting.application.usecase;

import com.glauber.voting.application.dto.SessionDto;
import com.glauber.voting.domain.model.Agenda;
import com.glauber.voting.domain.model.Session;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Unidade - SessionUseCase")
class SessionUseCaseTest {

    @Mock
    private com.glauber.voting.application.boundary.SessionBoundary sessionBoundary;

    @InjectMocks
    private SessionUseCase sessionUseCase;

    @Test
    @DisplayName("Deve retornar as pautas de uma sessão")
    void shouldReturnAgendas() {
        var agendasDomain = List.of(new Agenda(1L, "Pauta 1"));
        var sessionDomainReturn = new Session(1L, "Sessao 1", agendasDomain, 5, LocalDateTime.now(ZoneId.of("UTC-3")), LocalDateTime.now(ZoneId.of("UTC-3")).plusMinutes(5));

        when(sessionBoundary.findById(1L)).thenReturn(sessionDomainReturn);

        SessionDto.AgendasResponse response = sessionUseCase.getAgendas(1L);

        assertEquals(1, response.getAgendas().size());
        assertEquals(1L, response.getAgendas().get(0).getId());
        assertEquals("Pauta 1", response.getAgendas().get(0).getTitle());
    }
}