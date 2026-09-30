package com.sachin.ai_farming_assistant.chat.controller;

import com.sachin.ai_farming_assistant.chat.dto.ChatRequest;
import com.sachin.ai_farming_assistant.chat.dto.ChatResponse;
import com.sachin.ai_farming_assistant.chat.service.GeminiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final GeminiService geminiService;

    public ChatController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {

        String response = geminiService.generateResponse(
                request.message()
        );

        return new ChatResponse(response);

    }

}
