package com.glauber.voting.infrastructure.persistence.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class VoteEntityTest {

    @Test
    void deveTestarGetterSetterEBuilder() {
        LocalDateTime agora = LocalDateTime.now();
        VoteEntity vote = VoteEntity.builder()
                .id(1L)
                .sessionId(10L)
                .agendaId(100L)
                .affiliatedId("12345678901")
                .choice("SIM")
                .createdAt(agora)
                .build();

        assertEquals(1L, vote.getId());
        assertEquals(10L, vote.getSessionId());
        assertEquals(100L, vote.getAgendaId());
        assertEquals("12345678901", vote.getAffiliatedId());
        assertEquals("SIM", vote.getChoice());
        assertEquals(agora, vote.getCreatedAt());

        vote.setChoice("NAO");
        assertEquals("NAO", vote.getChoice());
    }

    @Test
    void deveTestarEqualsEHashCode() {
        VoteEntity v1 = VoteEntity.builder().id(1L).build();
        VoteEntity v2 = VoteEntity.builder().id(1L).build();
        VoteEntity v3 = VoteEntity.builder().id(2L).build();

        assertEquals(v1, v2);
        assertNotEquals(v1, v3);
        assertEquals(v1.hashCode(), v2.hashCode());
    }
}