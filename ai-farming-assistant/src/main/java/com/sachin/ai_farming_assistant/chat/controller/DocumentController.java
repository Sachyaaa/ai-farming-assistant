package com.sachin.ai_farming_assistant.chat.controller;

import com.sachin.ai_farming_assistant.chat.dto.DocumentChunkData;
import com.sachin.ai_farming_assistant.chat.dto.RagResponse;
import com.sachin.ai_farming_assistant.chat.dto.RetrievedChunk;
import com.sachin.ai_farming_assistant.chat.entity.Document;
import com.sachin.ai_farming_assistant.chat.service.*;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentProcessingService documentProcessingService;
    private final EmbeddingService embeddingService;
    private final DocumentIngestionService documentIngestionService;
    private final RetrievalService retrievalService;
    private final RagService ragService;
    private final GeminiService geminiService;
    private final CurrentUserService currentUserService;

    public DocumentController(
            DocumentProcessingService documentProcessingService,
            EmbeddingService embeddingService,
            DocumentIngestionService documentIngestionService,
            RetrievalService retrievalService,
            RagService ragService, GeminiService geminiService,
            CurrentUserService currentUserService
    ) {
        this.documentProcessingService = documentProcessingService;
        this.embeddingService = embeddingService;
        this.documentIngestionService = documentIngestionService;
        this.retrievalService = retrievalService;
        this.ragService = ragService;
        this.geminiService = geminiService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/test-chunk")
    public List<DocumentChunkData> testChunk(
            @RequestBody String text
    ) {
        return documentProcessingService.chunkText(text);
    }

    @PostMapping("/test-embedding")
    public Map<String, Object> testEmbedding(
            @RequestBody String text
    ) {
        float[] embedding =
                embeddingService.generateEmbedding(text);

        return Map.of(
                "dimensions", embedding.length,
                "firstTenValues",
                java.util.Arrays.copyOf(embedding, 10)
        );
    }

    @PostMapping("/test-ingestion")
    public Map<String, Object> testIngestion(
            @RequestBody String text
    ) {

        Document document =
                documentIngestionService.ingestText(
                        "Pomegranate Farming Guide",
                        "test-document.txt",
                        "farming_guide",
                        "pomegranate",
                        text
                );

        return Map.of(
                "documentId", document.getId(),
                "message", "Document ingested successfully"
        );
    }

    @PostMapping("/test-retrieval")
    public List<RetrievedChunk> testRetrieval(
            @RequestBody String question,
            @RequestParam(defaultValue = "3") int topK
    ) {
        return retrievalService.retrieve(question, topK);
    }

    @PostMapping(
            value = "/test-rag",
            consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public RagResponse testRag(@RequestBody String question) {

        return ragService.answer(question);
    }

    @PostMapping(
            value = "/test-tool",
            consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Map<String, Object> testTool(
            @RequestBody String question,
            Authentication authentication
    ) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getPrincipal()
                instanceof String
                && "anonymousUser".equals(
                authentication.getPrincipal()
        )) {

            throw new AccessDeniedException(
                    "Authentication is required"
            );
        }

        Long authenticatedUserId =
                currentUserService.getAuthenticatedUserId(
                        authentication
                );

        String answer = geminiService.answerWithFarmTool(
                question,
                authenticatedUserId
        );

        Map<String, Object> result = new HashMap<>();
        result.put("question", question);
        result.put("response", answer);
        return result;
    }
}
