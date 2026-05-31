package com.glauber.voting.application.usecase;

import com.glauber.voting.application.boundary.VoteBoundary;
import com.glauber.voting.application.dto.VoteDto;
import com.glauber.voting.domain.model.Vote;
import com.glauber.voting.domain.model.VoteChoice;
import com.glauber.voting.domain.model.VoteResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VoteUseCaseTest {

    @Mock
    private VoteBoundary voteBoundary;

    @InjectMocks
    private VoteUseCase voteUseCase;

    @Test
    void deveComputarVotoSincronoComSucesso() {
        // Arrange
        Long sessionId = 1L;
        VoteDto.Request request = new VoteDto.Request();
        request.setAgendaId(10L);
        request.setCpf("12345678901");
        request.setChoice("SIM");

        Vote voteSalvo = new Vote(55L, sessionId, 10L, "12345678901", VoteChoice.SIM);
        when(voteBoundary.save(any(Vote.class))).thenReturn(voteSalvo);

        // Act
        VoteDto.Response response = voteUseCase.execute(sessionId, request);

        // Assert
        assertNotNull(response);
        assertEquals(55L, response.getId());
        assertEquals(sessionId, response.getSessionId());
        assertEquals(10L, response.getAgendaId());
        assertEquals("SIM", response.getChoice());
        verify(voteBoundary, times(1)).save(any(Vote.class));
    }

    @Test
    void deveRetornarResultadosContabilizadosComSucesso() {
        // Arrange
        Long sessionId = 1L;
        List<VoteResult> listaResultados = List.of(
                new VoteResult(10L, 5L, 3L) // pauta 10, 5 Sim, 3 Nao
        );
        when(voteBoundary.countResultsBySession(sessionId)).thenReturn(listaResultados);

        // Act
        VoteDto.ResultsResponse response = voteUseCase.getResults(sessionId);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getResults().size());
        VoteDto.AgendaResult resultDto = response.getResults().get(0);
        assertEquals(10L, resultDto.getAgendaId());
        assertEquals(5L, resultDto.getYesVotes());
        assertEquals(3L, resultDto.getNoVotes());
        assertEquals(8L, resultDto.getTotalVotes()); // 5 + 3 = 8
        verify(voteBoundary, times(1)).countResultsBySession(sessionId);
    }
}