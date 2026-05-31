package com.glauber.voting.infrastructure.controller.v1;

import com.glauber.voting.application.dto.VoteDto;
import com.glauber.voting.application.usecase.VoteUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/vote")
@Tag(name = "Votos de Pautas", description = "Registrar votos e consultar resultados")
public class VoteController {

    private final VoteUseCase voteUseCase;

    public VoteController(VoteUseCase voteUseCase) {
        this.voteUseCase = voteUseCase;
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar um voto", description = "Registra um voto para uma pauta dentro de uma sessão")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Voto registado com sucesso",
                    content = @Content(schema = @Schema(implementation = VoteDto.Response.class))),
            @ApiResponse(responseCode = "400", description = "Erro de regra de negócio ou validação", content = @Content),
            @ApiResponse(responseCode = "404", description = "Sessão não encontrada", content = @Content)
    })
    public VoteDto.Response submitVote(
            @Parameter(description = "ID da sessão") @PathVariable Long id,
            @Valid @RequestBody VoteDto.Request request) {

            return voteUseCase.execute(id, request);
    }

    @GetMapping("/{id}/results")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Contabilizar resultados", description = "Retorna a contabilização de votos por pauta em uma sessão")
    public VoteDto.ResultsResponse getResults(
            @Parameter(description = "ID da sessão") @PathVariable Long id) {
        return voteUseCase.getResults(id);
    }
}

