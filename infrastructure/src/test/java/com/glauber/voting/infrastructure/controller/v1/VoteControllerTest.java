package com.glauber.voting.infrastructure.controller.v1;

import com.glauber.voting.application.exception.SessionException;
import com.glauber.voting.application.usecase.VoteUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VoteController.class)
@DisplayName("Testes de Unidade - VoteController")
class VoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VoteUseCase voteUseCase;

    @Test
    @DisplayName("Deve retornar warning quando sessão ainda não finalizada")
    void shouldReturnWarningWhenSessionNotFinished() throws Exception {
        Mockito.when(voteUseCase.getResults(anyLong()))
                .thenThrow(new SessionException("Sessão ainda não finalizada"));

        mockMvc.perform(get("/api/v1/vote/1/results")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.warning").value("Sessão ainda não finalizada"));
    }
}



