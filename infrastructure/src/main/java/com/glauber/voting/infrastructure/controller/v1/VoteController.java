package com.glauber.voting.infrastructure.controller.v1;

import com.glauber.voting.application.dto.VoteDto;
import com.glauber.voting.application.exception.SessionException;
import com.glauber.voting.application.usecase.VoteUseCase;
import com.glauber.voting.infrastructure.exception.CpfValidatorException;
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

import java.util.Collections;

@RestController
@RequestMapping("/api/v1/vote")
@Tag(name = "Votos de Pautas", description = "Registrar votos e consultar resultados")
public class VoteController {

    private final VoteUseCase voteUseCase;

    public VoteController(VoteUseCase voteUseCase) {
        this.voteUseCase = voteUseCase;
    }

    @PostMapping("/{id}")
    @Operation(summary = "Registrar um voto", description = "Registra um voto para uma pauta dentro de uma sessão")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Voto registrado com sucesso",
                    content = @Content(schema = @Schema(implementation = VoteDto.Response.class))),
            @ApiResponse(responseCode = "404", description = "Sessão ou pauta não encontrada", content = @Content)
    })
    public ResponseEntity<?> submitVote(
            @Parameter(description = "ID da sessão") @PathVariable Long id,
            @Valid @RequestBody(required = true) VoteDto.Request request) {

        try {
            VoteDto.Response response = voteUseCase.execute(id, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException | CpfValidatorException | SessionException ex) {
            java.util.Map<String, String> body = Collections.singletonMap("warning", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        }
    }

    @GetMapping("/{id}/results")
    @Operation(summary = "Contabilizar resultados", description = "Retorna a contabilização de votos por pauta em uma sessão")
    @ApiResponse(responseCode = "200", description = "Sucesso",
            content = @Content(schema = @Schema(implementation = VoteDto.ResultsResponse.class)))
    public ResponseEntity<?> getResults(
            @Parameter(description = "ID da sessão") @PathVariable Long id) {
        try {
            VoteDto.ResultsResponse response = voteUseCase.getResults(id);
            return ResponseEntity.ok(response);
        } catch (SessionException ex) {
            // Retorna um warning informando que a sessão ainda não foi finalizada
            java.util.Map<String, String> body = Collections.singletonMap("warning", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }
    }
}

