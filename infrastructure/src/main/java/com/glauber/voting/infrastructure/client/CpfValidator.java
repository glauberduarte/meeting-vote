package com.glauber.voting.infrastructure.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glauber.voting.infrastructure.exception.CpfValidatorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CpfValidator {
    public static final String ABLE_TO_VOTE = "ABLE_TO_VOTE";
    public static final String UNABLE_TO_VOTE = "UNABLE_TO_VOTE";

    @Value("${validator.cpf.url}")
    private String url;

    @Value("${validator.cpf.header}")
    private String header;

    public void isValid(String cpf) throws IOException, InterruptedException {
        String apiKey = System.getenv("API_KEY");

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + cpf))
                .header(header, apiKey)
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.body());
        JsonNode status = root.get("status");
        JsonNode success = root.get("success");

        if (status != null) {
            if (ABLE_TO_VOTE.equals(status.asText())) {
                System.out.println("CPF válido para votar: " + cpf);
            } else if (UNABLE_TO_VOTE.equals(status.asText())) {
                throw new CpfValidatorException("vote.cpf_unable_to_vote", "CPF não pode executar a operação: " + cpf, cpf);
            }
        }

        if (success != null && !success.asBoolean()) {
            JsonNode error = root.get("error");
            if (error != null) {
                throw new CpfValidatorException("empty", error.findValue("message").asText());
            }
            throw new CpfValidatorException("vote.cpf_not_found", "Dados do CPF não encontrados: " + cpf, cpf);
        }
    }
}
