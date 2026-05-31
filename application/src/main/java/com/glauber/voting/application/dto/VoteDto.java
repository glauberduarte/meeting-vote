package com.glauber.voting.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class VoteDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotNull(message = "A pauta não pode estar em branco")
        private Long agendaId;
        @NotBlank(message = "O CPF não pode estar em branco")
        @Pattern(regexp = "^\\d{11}$", message = "CPF inválido: deve conter 11 dígitos")
        private String cpf;
        @NotBlank(message = "Deve-se escolher SIM/NÃO para o voto")
        private String choice; // sim ou não
    }

    @Data
    @Builder
    public static class Response {
        private Long id;
        private Long sessionId;
        private Long agendaId;
        private String cpf;
        private String choice;
    }

    @Data
    @Builder
    public static class AgendaResult {
        private Long agendaId;
        private Long yesVotes;
        private Long noVotes;
        private Long totalVotes;
    }

    @Data
    @Builder
    public static class ResultsResponse {
        private List<AgendaResult> results;
    }
}

