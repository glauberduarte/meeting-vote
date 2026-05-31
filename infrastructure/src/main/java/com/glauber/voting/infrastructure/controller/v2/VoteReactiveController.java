package com.glauber.voting.infrastructure.controller.v2;

import com.glauber.voting.application.dto.VoteDto;
import com.glauber.voting.application.usecase.VoteReactiveUseCase;
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
import reactor.core.publisher.Mono;

import java.util.Collections;

@RestController
@RequestMapping("/api/v2/vote")
@Tag(name = "Votos de pautas de forma reativa", description = "Registrar votos de com melhor performance")
public class VoteReactiveController {

    private final VoteReactiveUseCase voteReactiveUseCase;

    public VoteReactiveController(VoteReactiveUseCase voteReactiveUseCase) {
        this.voteReactiveUseCase = voteReactiveUseCase;
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar um voto (Reativo)", description = "Registra um voto de forma assíncrona não-bloqueante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Voto aceito e enfileirado com sucesso",
                    content = @Content(schema = @Schema(implementation = VoteDto.Response.class))),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou regra de negócio", content = @Content),
            @ApiResponse(responseCode = "404", description = "Sessão não encontrada", content = @Content)
    })
    public Mono<VoteDto.Response> submitVote(
            @Parameter(description = "ID da sessão") @PathVariable Long id,
            @Valid @RequestBody VoteDto.Request request) {

        return voteReactiveUseCase.execute(id, request);
    }
}

