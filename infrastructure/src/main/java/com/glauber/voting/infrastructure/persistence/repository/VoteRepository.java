package com.glauber.voting.infrastructure.persistence.repository;

import com.glauber.voting.infrastructure.persistence.entity.VoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<VoteEntity, Long> {
	List<VoteEntity> findBySessionIdAndAgendaIdIn(Long sessionId, List<Long> agendaIds);
	boolean existsByAffiliatedIdAndSessionIdAndAgendaId(String affiliatedId, Long sessionId, Long agendaId);
}