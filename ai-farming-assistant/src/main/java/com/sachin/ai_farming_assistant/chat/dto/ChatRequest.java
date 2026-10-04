package com.sachin.ai_farming_assistant.chat.dto;

public record ChatRequest(
        Long conversationId,
        String message
) {}
