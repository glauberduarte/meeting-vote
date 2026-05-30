package com.glauber.voting.infrastructure.persistence.repository;

import com.glauber.voting.infrastructure.persistence.entity.AgendaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgendaRepository extends JpaRepository<AgendaEntity, Long> {
}