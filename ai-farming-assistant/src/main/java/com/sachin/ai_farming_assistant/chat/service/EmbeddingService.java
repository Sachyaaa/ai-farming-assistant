package com.sachin.ai_farming_assistant.chat.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class EmbeddingService {

    private final RestClient restClient;

    private final String apiKey;
    private final String embeddingModel;

    public EmbeddingService(
            RestClient.Builder restClientBuilder,
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.embedding-model}") String embeddingModel
    ) {
        this.restClient = restClientBuilder
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();

        this.apiKey = apiKey;
        this.embeddingModel = embeddingModel;
    }

    public float[] generateEmbedding(String text) {

        Map<String, Object> requestBody = Map.of(
                "content", Map.of(
                        "parts", List.of(
                                Map.of("text", text)
                        )
                )
        );

        Map response = restClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/{model}:embedContent")
                        .queryParam("key", apiKey)
                        .build(embeddingModel))
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        Map embedding = (Map) response.get("embedding");

        List<Double> values =
                (List<Double>) embedding.get("values");

        float[] result = new float[values.size()];

        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i).floatValue();
        }

        return result;
    }
}
