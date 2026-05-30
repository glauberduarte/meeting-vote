package com.glauber.voting.infrastructure.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glauber.voting.application.dto.AgendaDto;
import com.glauber.voting.application.dto.SessionDto;
import com.glauber.voting.application.usecase.SessionUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SessionController.class)
@DisplayName("Testes de Unidade - SessionController")
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SessionUseCase sessionUseCase;

    @Test
    @DisplayName("Deve retornar as pautas associadas a uma sessão")
    void shouldReturnSessionAgendas() throws Exception {
        var agenda = AgendaDto.Response.builder().id(1L).title("Pauta 1").build();
        var responseDto = SessionDto.AgendasResponse.builder().agendas(List.of(agenda)).build();

        Mockito.when(sessionUseCase.getAgendas(anyLong()))
                .thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/session/1/agendas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agendas[0].id").value(1))
                .andExpect(jsonPath("$.agendas[0].title").value("Pauta 1"));
    }
}