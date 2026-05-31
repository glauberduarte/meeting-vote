package com.glauber.voting.infrastructure.config;

/**
 * Representa o evento de voto que trafega de forma assíncrona no pipeline reativo.
 * Todos os parâmetros são obrigatórios para a composição da restrição de chave.
 */
public record VoteEvent(
    Long agendaId,
    Long sessionId,
    String affiliatedId,
    String choice
) {}