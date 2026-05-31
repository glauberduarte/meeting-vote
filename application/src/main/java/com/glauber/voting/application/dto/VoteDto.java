package com.glauber.voting.application.dto;

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
        private Long agendaId;
        private String cpf;
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

