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
    @Operation(summary = "Registrar um voto (Reativo)", description = "Registra um voto de forma assíncrona não-bloqueante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Voto aceito e enfileirado com sucesso",
                    content = @Content(schema = @Schema(implementation = VoteDto.Response.class))),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou regra de negócio", content = @Content)
    })
    public Mono<ResponseEntity<?>> submitVote(
            @Parameter(description = "ID da sessão") @PathVariable Long id,
            @Valid @RequestBody(required = true) VoteDto.Request request) {

        return voteReactiveUseCase.execute(id, request)
                .<ResponseEntity<?>>map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                // Captura os erros reativos e converte no formato HTTP
                .onErrorResume(ex -> {
                    java.util.Map<String, String> body = Collections.singletonMap("warning", ex.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body));
                });
    }
}

