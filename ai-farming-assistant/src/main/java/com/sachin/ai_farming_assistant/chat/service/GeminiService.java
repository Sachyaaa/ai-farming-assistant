package com.sachin.ai_farming_assistant.chat.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.Map;

@Service
public class GeminiService {

    private final RestClient restClient;

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    public GeminiService() {
        this.restClient = RestClient.builder().build();
    }

    public String generateResponse(String message) {

        String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                + model
                + ":generateContent";

        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of(
                                "parts", new Object[]{
                                        Map.of("text", message)
                                }
                        )
                }
        );

        Map response = restClient.post()
                .uri(url)
                .header("x-goog-api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        return extractText(response);
    }

    private String extractText(Map response) {

        var candidates = (java.util.List<Map<String, Object>>) response.get("candidates");

        if (candidates == null || candidates.isEmpty()) {
            throw new RuntimeException("Gemini returned no candidates");
        }

        var content = (Map<String, Object>) candidates
                .get(0)
                .get("content");

        var parts = (java.util.List<Map<String, Object>>) content
                .get("parts");

        return (String) parts.getFirst().get("text");

    }

}
