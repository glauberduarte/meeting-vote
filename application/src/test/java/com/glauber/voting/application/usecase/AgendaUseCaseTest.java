package com.glauber.voting.application.usecase;

import com.glauber.voting.application.boundary.AgendaBoundary;
import com.glauber.voting.application.dto.AgendaDto;
import com.glauber.voting.domain.model.Agenda;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendaUseCaseTest {

    @Mock
    private AgendaBoundary agendaBoundary;

    @InjectMocks
    private AgendaUseCase agendaUseCase;

    @Test
    void deveCriarPautaComSucesso() {
        // Arrange
        AgendaDto.Request request = new AgendaDto.Request();
        request.setTitle("Pauta de Teste");

        Agenda agendaSalva = new Agenda(1L, "Pauta de Teste");
        when(agendaBoundary.save(any(Agenda.class))).thenReturn(agendaSalva);

        // Act
        AgendaDto.Response response = agendaUseCase.execute(request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Pauta de Teste", response.getTitle());
        verify(agendaBoundary, times(1)).save(any(Agenda.class));
    }
}