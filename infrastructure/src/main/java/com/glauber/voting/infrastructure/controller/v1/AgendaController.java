package com.glauber.voting.infrastructure.controller.v1;

import com.glauber.voting.application.dto.AgendaDto;
import com.glauber.voting.application.usecase.AgendaUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Esta abordagem é altamente recomendada para aplicações, pois permite manter a compatibilidade com versões antigas
 * do aplicativo instaladas nos dispositivos dos usuários, facilitando o gerenciamento de cache nos controllers de API.
 */
@RestController
@RequestMapping("/api/v1/agendas")
@Tag(name = "Pautas de Votações", description = "Gerenciamento de pautas, sessões de votação e cômputo de votos")
public class AgendaController {

    private final AgendaUseCase agendaUseCase;

    public AgendaController(AgendaUseCase agendaUseCase) {
        this.agendaUseCase = agendaUseCase;
    }

    @PostMapping
    @Operation(summary = "Cadastrar uma nova pauta", description = "Cria uma nova pauta no sistema para futura votação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pauta criada com sucesso",
                    content = @Content(schema = @Schema(implementation = AgendaDto.Response.class)))
    })
    public ResponseEntity<AgendaDto.Response> createAgenda(@Valid @RequestBody AgendaDto.Request request) {
        AgendaDto.Response response = agendaUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}