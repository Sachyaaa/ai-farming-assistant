package com.sachin.ai_farming_assistant.chat.service;

import com.sachin.ai_farming_assistant.chat.dto.DocumentChunkData;
import com.sachin.ai_farming_assistant.chat.entity.Document;
import com.sachin.ai_farming_assistant.chat.entity.DocumentChunk;
import com.sachin.ai_farming_assistant.chat.repository.DocumentChunkRepository;
import com.sachin.ai_farming_assistant.chat.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DocumentIngestionService {

    private final DocumentProcessingService documentProcessingService;
    private final EmbeddingService embeddingService;
    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository documentChunkRepository;

    public DocumentIngestionService(
            DocumentProcessingService documentProcessingService,
            EmbeddingService embeddingService,
            DocumentRepository documentRepository,
            DocumentChunkRepository documentChunkRepository
    ) {
        this.documentProcessingService = documentProcessingService;
        this.embeddingService = embeddingService;
        this.documentRepository = documentRepository;
        this.documentChunkRepository = documentChunkRepository;
    }

    @Transactional
    public Document ingestText(
            String title,
            String source,
            String documentType,
            String crop,
            String text
    ) {

        // 1. Create document
        Document document = new Document(
                title,
                source,
                documentType,
                crop
        );

        document = documentRepository.save(document);

        // 2. Clean + chunk
        List<DocumentChunkData> chunks =
                documentProcessingService.chunkText(text);

        // 3. Generate embeddings + save chunks
        for (DocumentChunkData chunkData : chunks) {

            float[] embedding =
                    embeddingService.generateEmbedding(
                            chunkData.content()
                    );

            String metadata = """
                    {
                        "source": "%s",
                        "crop": "%s",
                        "chunk_index": %d
                    }
                    """.formatted(
                    source,
                    crop,
                    chunkData.chunkIndex()
            );

            DocumentChunk documentChunk =
                    new DocumentChunk(
                            document,
                            chunkData.chunkIndex(),
                            chunkData.content(),
                            embedding,
                            metadata
                    );

            documentChunkRepository.save(documentChunk);
        }

        return document;
    }
}