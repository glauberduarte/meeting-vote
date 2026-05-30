package com.glauber.voting.application.dto;

import com.glauber.voting.domain.model.Agenda;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class SessionDto {

    @Data
    public static class OpenRequest {
        private Integer durationInMinutes;
    }

    @Data
    public static class Request {
        private String title;
        private Integer durationInMinutes;
        private List<AgendaDto.Response> agendas;
    }

    @Data
    @Builder
    public static class Response {
        private Long id;
        private String title;
        private Integer durationInMinutes;
        private LocalDateTime openingTime;
        private LocalDateTime closingTime;
        private List<AgendaDto.Response> agendas;
    }
    
    @Data
    @Builder
    public static class AgendasResponse {
        private List<AgendaDto.Response> agendas;
    }

    public static List<Agenda> toDomain(List<AgendaDto.Response> agendaResponses) {
        return agendaResponses.stream()
                .map(AgendaDto::toDomain)
                .toList();
    }
}