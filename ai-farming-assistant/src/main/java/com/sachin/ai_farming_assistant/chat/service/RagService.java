package com.sachin.ai_farming_assistant.chat.service;

import com.sachin.ai_farming_assistant.chat.dto.RagResponse;
import com.sachin.ai_farming_assistant.chat.dto.RagSource;
import com.sachin.ai_farming_assistant.chat.dto.RetrievedChunk;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagService {

    private final RetrievalService retrievalService;
    private final GeminiService geminiService;

    public RagService(
            RetrievalService retrievalService,
            GeminiService geminiService
    ) {
        this.retrievalService = retrievalService;
        this.geminiService = geminiService;
    }

    /**
     * Generate an answer using retrieved farming documents.
     */
    public RagResponse answer(String question) {

        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException(
                    "Question must not be empty"
            );
        }

        // Step 1: Retrieve the three most relevant chunks.
        List<RetrievedChunk> chunks =
                retrievalService.retrieve(question, 3);

        // Step 2: Build context from retrieved documents.
        String context = buildContext(chunks);

        // Step 3: Ask Gemini to generate a grounded answer.
        String answer =
                geminiService.generateRagAnswer(
                        question,
                        context
                );

        // Step 4: Build source references.
        List<RagSource> sources = chunks.stream()
                .map(chunk -> new RagSource(
                        chunk.documentId(),
                        chunk.chunkId(),
                        chunk.source(),
                        chunk.crop()
                ))
                .toList();

        // Step 5: Return the answer and source references.
        return new RagResponse(answer, sources);
    }

    /**
     * Format retrieved chunks for the Gemini prompt.
     */
    private String buildContext(List<RetrievedChunk> chunks) {

        if (chunks == null || chunks.isEmpty()) {
            return "No relevant information was found "
                    + "in the available documents.";
        }

        StringBuilder context = new StringBuilder();

        for (int i = 0; i < chunks.size(); i++) {

            RetrievedChunk chunk = chunks.get(i);

            context.append("[Source ")
                    .append(i + 1)
                    .append("]\n");

            context.append("Document ID: ")
                    .append(chunk.documentId())
                    .append("\n");

            context.append("Chunk ID: ")
                    .append(chunk.chunkId())
                    .append("\n");

            context.append("Source: ")
                    .append(chunk.source())
                    .append("\n");

            context.append("Crop: ")
                    .append(chunk.crop())
                    .append("\n");

            context.append("Content:\n")
                    .append(chunk.content())
                    .append("\n\n");
        }

        return context.toString();
    }
}