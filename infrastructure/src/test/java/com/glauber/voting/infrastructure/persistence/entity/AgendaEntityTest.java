package com.glauber.voting.infrastructure.persistence.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
class AgendaEntityTest {

    @Test
    void deveTestarGetterSetterEBuilder() {
        AgendaEntity entity = AgendaEntity.builder()
                .id(1L)
                .title("Pauta de Teste")
                .build();

        assertEquals(1L, entity.getId());
        assertEquals("Pauta de Teste", entity.getTitle());

        entity.setId(2L);
        entity.setTitle("Nova Pauta");

        assertEquals(2L, entity.getId());
        assertEquals("Nova Pauta", entity.getTitle());
    }

    @Test
    void deveTestarEqualsEHashCode() {
        // O Equals do Lombok está configurado com (onlyExplicitlyIncluded = true) no ID
        AgendaEntity entity1 = AgendaEntity.builder().id(1L).title("Pauta A").build();
        AgendaEntity entity2 = AgendaEntity.builder().id(1L).title("Pauta B").build();
        AgendaEntity entity3 = AgendaEntity.builder().id(2L).title("Pauta A").build();

        assertEquals(entity1, entity2); 
        assertNotEquals(entity1, entity3);
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }
}