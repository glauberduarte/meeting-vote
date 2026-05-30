package com.glauber.voting.domain.model;

public enum VoteChoice {
    SIM, NAO;

    public static VoteChoice fromString(String value) {
        if (value == null)
            throw new IllegalArgumentException("Voto não pode ser nulo.");

        String normalized = value.trim().toUpperCase();

        if (normalized.equals("SIM"))
            return SIM;

        if (normalized.equals("NÃO") || normalized.equals("NAO"))
            return NAO;

        throw new IllegalArgumentException("Voto inválido. Escolha entre 'Sim' ou 'Não'.");
    }
}