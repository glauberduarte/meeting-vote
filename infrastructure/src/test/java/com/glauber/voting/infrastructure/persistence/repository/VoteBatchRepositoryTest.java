package com.glauber.voting.infrastructure.persistence.repository;

import com.glauber.voting.infrastructure.config.VoteEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import reactor.core.publisher.Sinks;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteBatchRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private PreparedStatement preparedStatement;

    private Sinks.Many<VoteEvent> voteSink;
    private VoteBatchRepository voteBatchRepository;

    @BeforeEach
    void setUp() {
        // Instancia um Sink real para simular o comportamento de subscrição reativa
        voteSink = Sinks.many().multicast().onBackpressureBuffer();
        voteBatchRepository = new VoteBatchRepository(jdbcTemplate, voteSink);
    }

    @Test
    void deveExecutarBatchInsertEMapearParametrosComSucesso() throws InterruptedException, SQLException {
        // Arrange
        voteBatchRepository.init();
        
        CountDownLatch latch = new CountDownLatch(1);
        ArgumentCaptor<ParameterizedPreparedStatementSetter<VoteEvent>> setterCaptor =
                ArgumentCaptor.forClass(ParameterizedPreparedStatementSetter.class);

        // Intercepta a chamada do JdbcTemplate
        when(jdbcTemplate.batchUpdate(
                eq(VoteBatchRepository.INSERT_INTO_VOTES),
                any(List.class),
                anyInt(),
                setterCaptor.capture()
        )).thenAnswer(invocation -> {
            latch.countDown(); // Liberta o teste assim que o lote for processado
            return new int[][]{{1}};
        });

        VoteEvent event = new VoteEvent(100L, 10L, "12345678901", "SIM");

        // Act
        voteSink.tryEmitNext(event);

        // Aguarda até 1.5 segundos para dar tempo ao bufferTimeout(1s) de disparar o lote
        latch.await(1500, TimeUnit.MILLISECONDS);

        // Executa manualmente o Setter capturado para testar e cobrir a lógica interna da expressão Lambda
        ParameterizedPreparedStatementSetter<VoteEvent> setter = setterCaptor.getValue();
        setter.setValues(preparedStatement, event);

        // Assert
        verify(jdbcTemplate, times(1)).batchUpdate(
                eq(VoteBatchRepository.INSERT_INTO_VOTES),
                any(List.class),
                eq(1),
                any(ParameterizedPreparedStatementSetter.class)
        );

        // Valida se os parâmetros do mapeamento SQL foram preenchidos corretamente
        verify(preparedStatement).setLong(1, 100L);
        verify(preparedStatement).setString(2, "12345678901");
        verify(preparedStatement).setLong(3, 10L);
        verify(preparedStatement).setString(4, "SIM");
    }
}