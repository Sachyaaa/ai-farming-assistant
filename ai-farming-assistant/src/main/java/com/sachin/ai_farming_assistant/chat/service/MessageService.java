package com.sachin.ai_farming_assistant.chat.service;

import com.sachin.ai_farming_assistant.chat.entity.Message;
import com.sachin.ai_farming_assistant.chat.entity.MessageRole;
import com.sachin.ai_farming_assistant.chat.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message saveUserMessage(
            Long conversationId,
            String content
    ) {
        Message message = new Message(
                conversationId,
                MessageRole.USER,
                content,
                LocalDateTime.now()
        );

        return messageRepository.save(message);
    }

    public Message saveAssistantMessage(
            Long conversationId,
            String content
    ) {
        Message message = new Message(
                conversationId,
                MessageRole.ASSISTANT,
                content,
                LocalDateTime.now()
        );

        return messageRepository.save(message);
    }

    public List<Message> getConversationMessages(
            Long conversationId
    ) {
        return messageRepository
                .findByConversationIdOrderByCreatedAtAsc(
                        conversationId
                );
    }
}