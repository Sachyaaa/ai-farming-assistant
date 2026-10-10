package com.sachin.ai_farming_assistant.chat.dto;

import java.util.List;

public record RagResponse(
        String answer,
        List<RagSource> sources
) {
}