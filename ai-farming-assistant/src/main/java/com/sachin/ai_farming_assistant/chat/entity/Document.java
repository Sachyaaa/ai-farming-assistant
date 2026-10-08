package com.sachin.ai_farming_assistant.chat.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Getter
@NoArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 500)
    private String source;

    @Column(name = "document_type", length = 50)
    private String documentType;

    @Column(length = 100)
    private String crop;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Document(
            String title,
            String source,
            String documentType,
            String crop
    ) {
        this.title = title;
        this.source = source;
        this.documentType = documentType;
        this.crop = crop;
        this.createdAt = LocalDateTime.now();
    }
}
