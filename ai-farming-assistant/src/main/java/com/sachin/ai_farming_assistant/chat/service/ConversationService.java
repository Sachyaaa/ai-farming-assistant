package com.sachin.ai_farming_assistant.chat.service;

import com.sachin.ai_farming_assistant.chat.entity.Conversation;
import com.sachin.ai_farming_assistant.chat.repository.ConversationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;

    public ConversationService(
            ConversationRepository conversationRepository
    ) {
        this.conversationRepository = conversationRepository;
    }

    public Conversation createConversation(Long userId) {

        LocalDateTime now = LocalDateTime.now();

        Conversation conversation = new Conversation(
                userId,
                now,
                now
        );

        return conversationRepository.save(conversation);
    }

    public Conversation getConversation(Long conversationId) {

        return conversationRepository
                .findById(conversationId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Conversation not found: " + conversationId
                        )
                );
    }

    public List<Conversation> getUserConversations(Long userId) {

        return conversationRepository
                .findByUserIdOrderByUpdatedAtDesc(userId);
    }

    public Conversation getUserConversation(
            Long conversationId,
            Long userId
    ) {

        return conversationRepository
                .findByIdAndUserId(conversationId, userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Conversation not found"
                        )
                );
    }
}