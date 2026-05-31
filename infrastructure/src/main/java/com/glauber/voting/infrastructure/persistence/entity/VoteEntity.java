package com.glauber.voting.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Entity
@Table(name = "votes", uniqueConstraints = {
    @UniqueConstraint(
        name = "uk_session_agenda_affiliated",
        columnNames = {"session_id", "agenda_id", "affiliated_id"}
    )
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class VoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private Long sessionId;

    @Column(nullable = false)
    private Long agendaId;

    @Column(nullable = false)
    private String affiliatedId;

    @Column(nullable = false)
    private String choice;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

}
