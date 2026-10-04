package com.sachin.ai_farming_assistant.chat.service;

import com.sachin.ai_farming_assistant.chat.dto.CropQuery;
import com.sachin.ai_farming_assistant.chat.entity.Message;
import com.sachin.ai_farming_assistant.chat.entity.MessageRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final MessageService messageService;
    private final ConversationService conversationService;

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    private static final String SYSTEM_INSTRUCTION = """
            You are an AI farming assistant.

            Provide practical and easy-to-understand farming guidance.

            Rules:
            1. Use the farmer context when relevant.
            2. Do not invent farm-specific facts.
            3. If important information is missing, clearly identify it.
            4. Do not present uncertain information as certain.
            5. Keep recommendations practical.

            Structure your response using these sections:

            Summary
            Key considerations
            Missing information
            Next steps
            """;

    private static final String FARMER_CONTEXT = """
            <farmer_context>
            Location: Maharashtra, India
            Farm size: 2 acres
            Soil type: gravely soil
            Irrigation: Drip irrigation
            Current crop: Pomegranate
            </farmer_context>
            """;

    public GeminiService(
            ObjectMapper objectMapper,
            MessageService messageService,
            ConversationService conversationService
    ) {
        this.restClient = RestClient.builder().build();
        this.objectMapper = objectMapper;
        this.messageService = messageService;
        this.conversationService = conversationService;
    }

    /*
     * ---------------------------------------------------------
     * NORMAL GEMINI RESPONSE
     * ---------------------------------------------------------
     */

    public String generateResponse(String message) {

        String url =
                "https://generativelanguage.googleapis.com/v1beta/models/"
                        + model
                        + ":generateContent";

        String prompt = """
                %s

                <user_message>
                %s
                </user_message>
                """.formatted(
                FARMER_CONTEXT,
                message
        );

        Map<String, Object> requestBody = Map.of(
                "systemInstruction", Map.of(
                        "parts", new Object[]{
                                Map.of(
                                        "text",
                                        SYSTEM_INSTRUCTION
                                )
                        }
                ),
                "contents", new Object[]{
                        Map.of(
                                "parts", new Object[]{
                                        Map.of(
                                                "text",
                                                prompt
                                        )
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

    /*
     * ---------------------------------------------------------
     * CROP QUERY CLASSIFICATION
     * ---------------------------------------------------------
     */

    public CropQuery classifyCropQuery(String message) {

        String systemInstruction = """
                You are a farming query classification system.

                Classify the user's query into exactly one category.

                Allowed categories:
                - CROP_HEALTH
                - IRRIGATION
                - FERTILIZER
                - WEATHER
                - PEST_CONTROL
                - GENERAL_FARMING

                Allowed urgency values:
                - LOW
                - MEDIUM
                - HIGH

                Return ONLY valid JSON.

                Required JSON structure:

                {
                  "category": "CROP_HEALTH",
                  "crop": "tomato",
                  "urgency": "MEDIUM",
                  "needsMoreInformation": true
                }

                Rules:
                - category must be one of the allowed categories.
                - urgency must be LOW, MEDIUM, or HIGH.
                - crop should be null if the crop is not mentioned.
                - needsMoreInformation must be true or false.
                - Do not add additional fields.
                """;

        String prompt = """
                <farmer_context>
                %s
                </farmer_context>

                <user_message>
                %s
                </user_message>
                """.formatted(
                FARMER_CONTEXT,
                message
        );

        String url =
                "https://generativelanguage.googleapis.com/v1beta/models/"
                        + model
                        + ":generateContent";

        Map<String, Object> requestBody = Map.of(
                "systemInstruction", Map.of(
                        "parts", new Object[]{
                                Map.of(
                                        "text",
                                        systemInstruction
                                )
                        }
                ),
                "contents", new Object[]{
                        Map.of(
                                "parts", new Object[]{
                                        Map.of(
                                                "text",
                                                prompt
                                        )
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

        if (response == null) {
            throw new IllegalStateException(
                    "Gemini returned an empty response"
            );
        }

        String json = extractText(response);

        try {

            return objectMapper.readValue(
                    json,
                    CropQuery.class
            );

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Gemini returned invalid classification JSON",
                    e
            );
        }
    }

    /*
     * ---------------------------------------------------------
     * STREAMING CHAT WITH CONVERSATION MEMORY
     * ---------------------------------------------------------
     */

    public SseEmitter streamResponse(
            Long conversationId,
            Long userId,
            String message
    ) {

        /*
         * 1. Validate conversation
         *
         * If the conversation does not exist,
         * ConversationService throws an exception.
         */
        conversationService.getUserConversation(
                conversationId,
                userId
        );

        /*
         * 2. Save the user's message
         */
        messageService.saveUserMessage(
                conversationId,
                message
        );

        /*
         * 3. Load complete conversation history
         */
        List<Message> history =
                messageService.getConversationMessages(
                        conversationId
                );

        /*
         * 4. Convert our database messages
         *    into Gemini's conversation format.
         */
        List<Map<String, Serializable>> contents =
                buildGeminiHistory(history);

        SseEmitter emitter =
                new SseEmitter(120_000L);

        Thread.startVirtualThread(() -> {

            /*
             * Collect all streamed chunks.
             *
             * We send chunks immediately to React,
             * but save ONE complete assistant message
             * to PostgreSQL after streaming finishes.
             */
            StringBuilder assistantResponse =
                    new StringBuilder();

            try {

                String url =
                        "https://generativelanguage.googleapis.com/v1beta/models/"
                                + model
                                + ":streamGenerateContent?alt=sse";

                /*
                 * 5. Build Gemini request using
                 *    the complete conversation history.
                 */
                Map<String, Object> requestBody =
                        Map.of(
                                "systemInstruction",
                                Map.of(
                                        "parts",
                                        new Object[]{
                                                Map.of(
                                                        "text",
                                                        SYSTEM_INSTRUCTION
                                                                + "\n\n"
                                                                + FARMER_CONTEXT
                                                )
                                        }
                                ),
                                "contents",
                                contents
                        );

                String jsonBody =
                        objectMapper.writeValueAsString(
                                requestBody
                        );

                HttpRequest request =
                        HttpRequest.newBuilder()
                                .uri(URI.create(url))
                                .header(
                                        "x-goog-api-key",
                                        apiKey
                                )
                                .header(
                                        HttpHeaders.CONTENT_TYPE,
                                        MediaType.APPLICATION_JSON_VALUE
                                )
                                .POST(
                                        HttpRequest.BodyPublishers
                                                .ofString(jsonBody)
                                )
                                .build();

                HttpClient httpClient =
                        HttpClient.newHttpClient();

                HttpResponse<InputStream> response =
                        httpClient.send(
                                request,
                                HttpResponse.BodyHandlers
                                        .ofInputStream()
                        );

                /*
                 * 6. Handle Gemini HTTP errors
                 */
                if (response.statusCode() < 200 ||
                        response.statusCode() >= 300) {

                    String errorBody =
                            new String(
                                    response.body()
                                            .readAllBytes(),
                                    StandardCharsets.UTF_8
                            );

                    emitter.send(
                            SseEmitter.event()
                                    .name("error")
                                    .data(
                                            "Gemini API error: "
                                                    + errorBody
                                    )
                    );

                    emitter.complete();
                    return;
                }

                /*
                 * 7. Read Gemini SSE stream
                 */
                try (
                        InputStream inputStream =
                                response.body();

                        BufferedReader reader =
                                new BufferedReader(
                                        new InputStreamReader(
                                                inputStream,
                                                StandardCharsets.UTF_8
                                        )
                                )
                ) {

                    String line;

                    while (
                            (line = reader.readLine())
                                    != null
                    ) {

                        /*
                         * Gemini SSE events look like:
                         *
                         * data: {"candidates":[...]}
                         */
                        if (!line.startsWith("data:")) {
                            continue;
                        }

                        String json =
                                line.substring(5).trim();

                        if (json.isEmpty()) {
                            continue;
                        }

                        JsonNode root =
                                objectMapper.readTree(json);

                        JsonNode candidates =
                                root.path("candidates");

                        if (!candidates.isArray() ||
                                candidates.isEmpty()) {
                            continue;
                        }

                        JsonNode parts =
                                candidates
                                        .get(0)
                                        .path("content")
                                        .path("parts");

                        if (!parts.isArray()) {
                            continue;
                        }

                        for (JsonNode part : parts) {

                            JsonNode textNode =
                                    part.get("text");

                            if (textNode == null ||
                                    textNode.isNull()) {
                                continue;
                            }

                            String text =
                                    textNode.asText();

                            /*
                             * Add chunk to complete response.
                             */
                            assistantResponse.append(text);

                            /*
                             * Immediately send chunk
                             * to React.
                             */
                            sendChunk(
                                    emitter,
                                    text
                            );
                        }
                    }
                }

                /*
                 * 8. Save ONE complete assistant message
                 */
                messageService.saveAssistantMessage(
                        conversationId,
                        assistantResponse.toString()
                );

                /*
                 * 9. Tell React that streaming is complete.
                 */
                emitter.send(
                        SseEmitter.event()
                                .name("done")
                                .data("[DONE]")
                );

                emitter.complete();

            } catch (Exception e) {

                try {

                    emitter.send(
                            SseEmitter.event()
                                    .name("error")
                                    .data(
                                            "AI service unavailable"
                                    )
                    );

                } catch (IOException ignored) {
                    /*
                     * Client may already have disconnected.
                     */
                }

                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    /*
     * ---------------------------------------------------------
     * SEND SSE CHUNK
     * ---------------------------------------------------------
     */

    private void sendChunk(
            SseEmitter emitter,
            String text
    ) {

        try {

            emitter.send(
                    SseEmitter.event()
                            .name("message")
                            .data(text)
            );

        } catch (IOException e) {

            emitter.completeWithError(e);
        }
    }

    /*
     * ---------------------------------------------------------
     * CONVERT DATABASE HISTORY → GEMINI HISTORY
     * ---------------------------------------------------------
     */

    private List<Map<String, Serializable>> buildGeminiHistory(
            List<Message> messages
    ) {

        return messages.stream()
                .map(message -> {

                    String role =
                            message.getRole()
                                    == MessageRole.USER
                                    ? "user"
                                    : "model";

                    return Map.of(
                            "role",
                            role,

                            "parts",
                            new Object[]{
                                    Map.of(
                                            "text",
                                            message.getContent()
                                    )
                            }
                    );
                })
                .toList();
    }

    /*
     * ---------------------------------------------------------
     * EXTRACT NORMAL GEMINI RESPONSE
     * ---------------------------------------------------------
     */

    private String extractText(Map response) {

        if (response == null) {
            throw new IllegalStateException(
                    "Gemini returned an empty response"
            );
        }

        var candidates =
                (List<Map<String, Object>>)
                        response.get("candidates");

        if (candidates == null ||
                candidates.isEmpty()) {

            throw new IllegalStateException(
                    "Gemini returned no candidates"
            );
        }

        var content =
                (Map<String, Object>)
                        candidates
                                .get(0)
                                .get("content");

        if (content == null) {
            throw new IllegalStateException(
                    "Gemini response contains no content"
            );
        }

        var parts =
                (List<Map<String, Object>>)
                        content.get("parts");

        if (parts == null ||
                parts.isEmpty()) {

            throw new IllegalStateException(
                    "Gemini response contains no parts"
            );
        }

        return (String)
                parts.getFirst().get("text");
    }
}