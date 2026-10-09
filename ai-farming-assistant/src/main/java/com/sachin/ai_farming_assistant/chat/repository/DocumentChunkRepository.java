package com.sachin.ai_farming_assistant.chat.repository;

import com.sachin.ai_farming_assistant.chat.entity.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentChunkRepository
        extends JpaRepository<DocumentChunk, Long> {

    List<DocumentChunk> findByDocumentIdOrderByChunkIndexAsc(
            Long documentId
    );

    @Query(value = """
    SELECT
        dc.id AS chunk_id,
        dc.document_id AS document_id,
        dc.content AS content,
        d.source AS source,
        d.crop AS crop,
        dc.embedding <=> CAST(:embedding AS vector) AS distance
    FROM document_chunks dc
    JOIN documents d ON d.id = dc.document_id
    WHERE dc.embedding IS NOT NULL
    ORDER BY dc.embedding <=> CAST(:embedding AS vector)
    LIMIT :topK
    """, nativeQuery = true)
    List<Object[]> findSimilarChunks(
            @Param("embedding") String embedding,
            @Param("topK") int topK
    );
}
