package com.sachin.ai_farming_assistant.chat.controller;

import com.sachin.ai_farming_assistant.chat.entity.Conversation;
import com.sachin.ai_farming_assistant.chat.entity.Message;
import com.sachin.ai_farming_assistant.chat.entity.User;
import com.sachin.ai_farming_assistant.chat.service.AuthService;
import com.sachin.ai_farming_assistant.chat.service.ConversationService;
import com.sachin.ai_farming_assistant.chat.service.MessageService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    private final MessageService messageService;

    private final AuthService authService;

    public ConversationController(
            ConversationService conversationService,
            MessageService messageService,
            AuthService authService
    ) {
        this.conversationService = conversationService;
        this.messageService = messageService;
        this.authService = authService;
    }

    @PostMapping
    public Conversation createConversation(
            Authentication authentication
    ) {

        User user = authService.findByEmail(
                authentication.getName()
        );

        return conversationService.createConversation(
                user.getId()
        );
    }

    @GetMapping
    public List<Conversation> getConversations(
            Authentication authentication
    ) {

        User user = authService.findByEmail(
                authentication.getName()
        );

        return conversationService.getUserConversations(
                user.getId()
        );
    }

    @GetMapping("/{conversationId}/messages")
    public List<Message> getMessages(
            @PathVariable Long conversationId,
            Authentication authentication
    ) {

        User user = authService.findByEmail(
                authentication.getName()
        );

        conversationService.getUserConversation(
                conversationId,
                user.getId()
        );

        return messageService
                .getConversationMessages(conversationId);
    }
}