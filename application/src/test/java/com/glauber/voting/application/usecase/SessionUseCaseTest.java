package com.glauber.voting.application.usecase;

import com.glauber.voting.application.boundary.SessionBoundary;
import com.glauber.voting.application.dto.SessionDto;
import com.glauber.voting.domain.model.Agenda;
import com.glauber.voting.domain.model.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class SessionUseCaseTest {

    @Mock
    private SessionBoundary sessionBoundary;

    @InjectMocks
    private SessionUseCase sessionUseCase;

    @Test
    void deveCriarSessaoComSucesso() {
        // Arrange
        SessionDto.Request request = new SessionDto.Request();
        request.setTitle("Assembleia Geral");
        request.setDurationInMinutes(60);
        request.setAgendas(new ArrayList<>()); // Evita NullPointerException se houver validação interna

        LocalDateTime agora = LocalDateTime.now();
        Session sessionSalva = new Session(1L, "Assembleia Geral", new ArrayList<>(), 60, agora, agora.plusMinutes(60));
        when(sessionBoundary.save(any(Session.class))).thenReturn(sessionSalva);

        // Act
        SessionDto.Response response = sessionUseCase.execute(request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Assembleia Geral", response.getTitle());
        assertEquals(60, request.getDurationInMinutes());
        verify(sessionBoundary, times(1)).save(any(Session.class));
    }

    @Test
    void deveRetornarPautasDeUmaSessaoComSucesso() {
        // Arrange
        Long sessionId = 1L;
        List<Agenda> agendasDomain = List.of(
                new Agenda(10L, "Pauta 1"),
                new Agenda(20L, "Pauta 2")
        );
        Session sessionMock = new Session(sessionId, "Sessão Ativa", agendasDomain, 30, LocalDateTime.now(), LocalDateTime.now().plusMinutes(30));

        when(sessionBoundary.findById(sessionId)).thenReturn(sessionMock);

        // Act
        SessionDto.AgendasResponse response = sessionUseCase.getAgendas(sessionId);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getAgendas().size());
        assertEquals(10L, response.getAgendas().get(0).getId());
        assertEquals("Pauta 1", response.getAgendas().get(0).getTitle());
        assertEquals(20L, response.getAgendas().get(1).getId());
        assertEquals("Pauta 2", response.getAgendas().get(1).getTitle());
        verify(sessionBoundary, times(1)).findById(sessionId);
    }
}