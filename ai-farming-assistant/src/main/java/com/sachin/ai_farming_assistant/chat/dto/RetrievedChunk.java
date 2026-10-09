package com.sachin.ai_farming_assistant.chat.dto;

public record RetrievedChunk(
        Long chunkId,
        Long documentId,
        String content,
        String source,
        String crop,
        double distance,
        double similarity
) {
}