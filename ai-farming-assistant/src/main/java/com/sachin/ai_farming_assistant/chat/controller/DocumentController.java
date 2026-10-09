package com.sachin.ai_farming_assistant.chat.controller;

import com.sachin.ai_farming_assistant.chat.dto.DocumentChunkData;
import com.sachin.ai_farming_assistant.chat.dto.RetrievedChunk;
import com.sachin.ai_farming_assistant.chat.entity.Document;
import com.sachin.ai_farming_assistant.chat.service.DocumentIngestionService;
import com.sachin.ai_farming_assistant.chat.service.DocumentProcessingService;
import com.sachin.ai_farming_assistant.chat.service.EmbeddingService;
import com.sachin.ai_farming_assistant.chat.service.RetrievalService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentProcessingService documentProcessingService;

    private final EmbeddingService embeddingService;

    private final DocumentIngestionService documentIngestionService;

    private final RetrievalService retrievalService;

    public DocumentController(
            DocumentProcessingService documentProcessingService,
            EmbeddingService embeddingService,
            DocumentIngestionService documentIngestionService,
            RetrievalService retrievalService
    ) {
        this.documentProcessingService = documentProcessingService;
        this.embeddingService = embeddingService;
        this.documentIngestionService = documentIngestionService;
        this.retrievalService = retrievalService;
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
}
