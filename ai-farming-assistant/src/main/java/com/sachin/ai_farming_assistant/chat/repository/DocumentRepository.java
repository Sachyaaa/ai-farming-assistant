package com.sachin.ai_farming_assistant.chat.repository;

import com.sachin.ai_farming_assistant.chat.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository
        extends JpaRepository<Document, Long> {
}
