package com.glauber.voting.infrastructure.persistence.repository;

import com.glauber.voting.infrastructure.persistence.entity.VoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<VoteEntity, Long> {
	java.util.List<VoteEntity> findBySessionIdAndAgendaIdIn(Long sessionId, java.util.List<Long> agendaIds);
}