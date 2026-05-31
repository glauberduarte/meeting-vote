package com.glauber.voting.application.usecase;

import com.glauber.voting.application.boundary.VoteReactiveBoundary;
import com.glauber.voting.application.dto.VoteDto;
import com.glauber.voting.application.exception.SessionException;
import com.glauber.voting.domain.model.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class VoteReactiveUseCaseTest {

    @Mock
    private VoteReactiveBoundary voteReactiveBoundary;

    private VoteReactiveUseCase voteReactiveUseCase;

    private VoteDto.Request request;
    private Long sessionId = 1L;

    @BeforeEach
    void setUp() {
        voteReactiveUseCase = new VoteReactiveUseCase(voteReactiveBoundary);
        
        request = new VoteDto.Request();
        request.setAgendaId(10L);
        request.setCpf("12345678901");
        request.setChoice("SIM");
    }

    @Test
    void deveLancarExcecaoQuandoAssociadoJaVotou() {
        // Arrange: Força hasVoted a retornar true
        when(voteReactiveBoundary.hasVoted(10L, sessionId, "12345678901")).thenReturn(Mono.just(true));

        // Act & Assert
        Mono<VoteDto.Response> result = voteReactiveUseCase.execute(sessionId, request);

        SessionException exception = assertThrows(SessionException.class, result::block);
        assertEquals("vote.cpf_already_voted", exception.getErrorCode());
        verify(voteReactiveBoundary, never()).getSession(anyLong());
    }

    @Test
    void deveLancarExcecaoQuandoSessaoNaoForEncontrada() {
        // Arrange
        when(voteReactiveBoundary.hasVoted(10L, sessionId, "12345678901")).thenReturn(Mono.just(false));
        when(voteReactiveBoundary.getSession(sessionId)).thenReturn(Mono.empty()); // Cache e BD vazios

        // Act & Assert
        Mono<VoteDto.Response> result = voteReactiveUseCase.execute(sessionId, request);

        SessionException exception = assertThrows(SessionException.class, result::block);
        assertEquals("session.not_found", exception.getErrorCode());
    }

    @Test
    void deveLancarExcecaoQuandoSessaoJaEstiverFechada() {
        // Arrange
        Session sessaoFechada = new Session(sessionId, "Título", null, 1, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(23));
        
        when(voteReactiveBoundary.hasVoted(10L, sessionId, "12345678901")).thenReturn(Mono.just(false));
        when(voteReactiveBoundary.getSession(sessionId)).thenReturn(Mono.just(sessaoFechada));

        // Act & Assert
        Mono<VoteDto.Response> result = voteReactiveUseCase.execute(sessionId, request);

        SessionException exception = assertThrows(SessionException.class, result::block);
        assertEquals("session.already_closed", exception.getErrorCode());
        verify(voteReactiveBoundary, never()).validateCpf(anyString());
    }

    @Test
    void deveProcessarEVotarComSucessoSeTudoEstiverValido() {
        // Arrange
        Session sessaoAberta = new Session(sessionId, "Título", null, 60, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        
        when(voteReactiveBoundary.hasVoted(10L, sessionId, "12345678901")).thenReturn(Mono.just(false));
        when(voteReactiveBoundary.getSession(sessionId)).thenReturn(Mono.just(sessaoAberta));
        when(voteReactiveBoundary.validateCpf("12345678901")).thenReturn(Mono.empty()); // Mono<Void> sucesso é Mono.empty()
        
        doNothing().when(voteReactiveBoundary).queueVote(10L, sessionId, "12345678901", "SIM");

        // Act
        VoteDto.Response response = voteReactiveUseCase.execute(sessionId, request).block();

        // Assert
        assertNotNull(response);
        assertEquals(10L, response.getAgendaId());
        assertEquals(sessionId, response.getSessionId());
        assertEquals("12345678901", response.getCpf());
        assertEquals("SIM", response.getChoice());
        
        verify(voteReactiveBoundary, times(1)).queueVote(10L, sessionId, "12345678901", "SIM");
    }
}