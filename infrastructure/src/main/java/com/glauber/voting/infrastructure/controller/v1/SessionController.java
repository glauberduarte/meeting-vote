package com.glauber.voting.infrastructure.controller.v1;

import com.glauber.voting.application.dto.SessionDto;
import com.glauber.voting.application.exception.SessionException;
import com.glauber.voting.application.usecase.SessionUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Esta abordagem é altamente recomendada para aplicações, pois permite manter a compatibilidade com versões antigas
 * do aplicativo instaladas nos dispositivos dos usuários, facilitando o gerenciamento de cache nos controllers de API.
 */
@RestController
@RequestMapping("/api/v1/session")
@Tag(name = "Controle de Assembléias de Votações", description = "Gerenciamento de assembléias de votação")
public class SessionController {

    private final SessionUseCase sessionUseCase;

    public SessionController(SessionUseCase sessionUseCase) {
        this.sessionUseCase = sessionUseCase;
    }

    @PostMapping
    @Operation(summary = "Abrir uma sessão de votação", description = "Abre uma sessão de votação para uma pauta específica. Duração padrão é de 1 minuto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sessão aberta com sucesso",
                    content = @Content(schema = @Schema(implementation = SessionDto.Response.class))),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada", content = @Content)
    })
    public ResponseEntity<SessionDto.Response> openSession(
            @RequestBody(required = false) SessionDto.Request request) {
        SessionDto.Response response = sessionUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/agendas")
    @Operation(summary = "Buscar pautas por sessão", description = "Retorna a lista de pautas ativas em uma sessão.")
    @ApiResponse(responseCode = "200", description = "Sucesso",
            content = @Content(schema = @Schema(implementation = SessionDto.AgendasResponse.class)))
    public ResponseEntity<SessionDto.AgendasResponse> getSessionAgendas(
            @Parameter(description = "ID da sessão/pauta") @PathVariable Long id) {
        try {
            SessionDto.AgendasResponse response = sessionUseCase.getAgendas(id);
            return ResponseEntity.ok(response);
        } catch (SessionException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}