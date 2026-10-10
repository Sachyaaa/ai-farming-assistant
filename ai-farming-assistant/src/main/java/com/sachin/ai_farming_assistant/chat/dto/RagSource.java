package com.sachin.ai_farming_assistant.chat.dto;

public record RagSource(
        Long documentId,
        Long chunkId,
        String source,
        String crop
) {
}