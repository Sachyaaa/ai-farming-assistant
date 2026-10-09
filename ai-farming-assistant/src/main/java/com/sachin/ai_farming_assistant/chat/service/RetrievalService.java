package com.sachin.ai_farming_assistant.chat.service;

import com.sachin.ai_farming_assistant.chat.dto.RetrievedChunk;
import com.sachin.ai_farming_assistant.chat.repository.DocumentChunkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class RetrievalService {

    private final EmbeddingService embeddingService;
    private final DocumentChunkRepository documentChunkRepository;

    public RetrievalService(
            EmbeddingService embeddingService,
            DocumentChunkRepository documentChunkRepository
    ) {
        this.embeddingService = embeddingService;
        this.documentChunkRepository = documentChunkRepository;
    }

    @Transactional(readOnly = true)
    public List<RetrievedChunk> retrieve(
            String question,
            int topK
    ) {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException(
                    "Question must not be empty"
            );
        }

        if (topK < 1 || topK > 10) {
            throw new IllegalArgumentException(
                    "topK must be between 1 and 10"
            );
        }

        // 1. Convert the question into a vector.
        float[] queryEmbedding =
                embeddingService.generateEmbedding(question);

        // 2. Serialize the vector for the native PostgreSQL query.
        String embeddingParameter = Arrays.toString(queryEmbedding)
                .replace(" ", "");

        // 3. Search PostgreSQL for the nearest chunks.
        List<Object[]> rows =
                documentChunkRepository.findSimilarChunks(
                        embeddingParameter,
                        topK
                );

        // 4. Convert database results into our response DTO.
        return rows.stream()
                .map(row -> {
                    double distance =
                            ((Number) row[5]).doubleValue();

                    return new RetrievedChunk(
                            ((Number) row[0]).longValue(),
                            ((Number) row[1]).longValue(),
                            (String) row[2],
                            (String) row[3],
                            (String) row[4],
                            distance,
                            1.0 - distance
                    );
                })
                .toList();
    }
}