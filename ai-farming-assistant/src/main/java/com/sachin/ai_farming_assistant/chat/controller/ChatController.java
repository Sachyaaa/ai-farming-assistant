package com.sachin.ai_farming_assistant.chat.controller;

import com.sachin.ai_farming_assistant.chat.dto.ChatRequest;
import com.sachin.ai_farming_assistant.chat.dto.ChatResponse;
import com.sachin.ai_farming_assistant.chat.dto.CropQuery;
import com.sachin.ai_farming_assistant.chat.entity.User;
import com.sachin.ai_farming_assistant.chat.service.AuthService;
import com.sachin.ai_farming_assistant.chat.service.GeminiService;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final GeminiService geminiService;
    private final AuthService authService;

    public ChatController(GeminiService geminiService, AuthService authService) {
        this.authService = authService;
        this.geminiService = geminiService;
    }

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {

        String response = geminiService.generateResponse(
                request.message()
        );

        return new ChatResponse(response);

    }

    @PostMapping("/classify")
    public CropQuery classify(@RequestBody ChatRequest request) {
        return geminiService.classifyCropQuery(
                request.message()
        );
    }

    @PostMapping(
            value = "/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public SseEmitter streamChat(
            @RequestBody ChatRequest request,
            Authentication authentication
    ) {

        User user = authService.findByEmail(
                authentication.getName()
        );

        return geminiService.streamResponse(
                request.conversationId(),
                user.getId(),
                request.message()
        );
    }

}
