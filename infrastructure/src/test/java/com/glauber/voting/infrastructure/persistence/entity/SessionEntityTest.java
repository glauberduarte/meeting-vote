package com.glauber.voting.infrastructure.persistence.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SessionEntityTest {

    @Test
    void deveTestarGetterSetterEBuilder() {
        LocalDateTime agora = LocalDateTime.now();
        AgendaEntity agenda = AgendaEntity.builder().id(1L).title("Pauta").build();

        SessionEntity session = SessionEntity.builder()
                .id(10L)
                .assemblyTitle("Assembleia Geral")
                .durationInMinutes(60)
                .openingTime(agora)
                .closingTime(agora.plusMinutes(60))
                .agendas(List.of(agenda))
                .build();

        assertEquals(10L, session.getId());
        assertEquals("Assembleia Geral", session.getAssemblyTitle());
        assertEquals(60, session.getDurationInMinutes());
        assertEquals(agora, session.getOpeningTime());
        assertEquals(agora.plusMinutes(60), session.getClosingTime());
        assertEquals(1, session.getAgendas().size());

        session.setAssemblyTitle("Sessão Extraordinária");
        assertEquals("Sessão Extraordinária", session.getAssemblyTitle());
    }

    @Test
    void deveTestarEqualsEHashCode() {
        SessionEntity s1 = SessionEntity.builder().id(10L).build();
        SessionEntity s2 = SessionEntity.builder().id(10L).build();
        SessionEntity s3 = SessionEntity.builder().id(20L).build();

        assertEquals(s1, s2);
        assertNotEquals(s1, s3);
        assertEquals(s1.hashCode(), s2.hashCode());
    }
}