package com.glauber.voting.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;

public class AgendaDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotBlank(message = "O título da pauta não pode estar em branco")
        private String title;
    }

    @Data
    @Builder
    public static class Response {
        private Long id;

        @JsonProperty(value = "title")
        private String title;
    }
}