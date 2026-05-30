package com.glauber.voting.infrastructure.persistence.entity;

import com.glauber.voting.domain.model.VoteChoice;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "votes")
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

}
