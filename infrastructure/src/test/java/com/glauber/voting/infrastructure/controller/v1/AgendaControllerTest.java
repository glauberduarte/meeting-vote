package com.glauber.voting.infrastructure.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glauber.voting.application.dto.AgendaDto;
import com.glauber.voting.application.usecase.AgendaUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AgendaController.class)
@DisplayName("Testes de Unidade - AgendaController")
class AgendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Isola o controller mockando a camada de aplicação
    @MockBean
    private AgendaUseCase agendaUseCase;

    @Test
    @DisplayName("Deve criar uma pauta com sucesso e retornar 201 Created")
    void shouldCreateAgendaWithSuccess() throws Exception {
        // Given (Dado)
        var requestDto = AgendaDto.Request.builder()
                .title("Assembleia de Investimentos")
                .build();

        var responseDto = AgendaDto.Response.builder()
                .id(123L)
                .title("Assembleia de Investimentos")
                .build();

        // Configura o comportamento do Mock da camada de aplicação
        Mockito.when(agendaUseCase.execute(any(AgendaDto.Request.class)))
               .thenReturn(responseDto);

        // When & Then (Quando e Então)
        mockMvc.perform(post("/api/v1/agendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(123L))
                .andExpect(jsonPath("$.title").value("Assembleia de Investimentos"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando o título for enviado em branco")
    void shouldReturnBadRequestWhenTitleIsBlank() throws Exception {
        // Given (Dado) - Título em branco viola a anotação @NotBlank do Jakarta Validation
        var invalidRequestDto = AgendaDto.Request.builder()
                .title("   ") 
                .build();

        // When & Then (Quando e Então)
        mockMvc.perform(post("/api/v1/agendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Garante que o caso de uso não foi acionado, pois o interceptor de validação agiu antes
        Mockito.verifyNoInteractions(agendaUseCase);
    }
}