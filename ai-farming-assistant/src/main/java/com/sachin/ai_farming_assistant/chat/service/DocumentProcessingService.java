package com.sachin.ai_farming_assistant.chat.service;

import com.sachin.ai_farming_assistant.chat.dto.DocumentChunkData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentProcessingService {

    private static final int DEFAULT_CHUNK_SIZE = 500;
    private static final int DEFAULT_OVERLAP = 100;

    public List<DocumentChunkData> chunkText(String text) {

        String cleanedText = cleanText(text);

        return createChunks(
                cleanedText,
                DEFAULT_CHUNK_SIZE,
                DEFAULT_OVERLAP
        );
    }

    private String cleanText(String text) {

        if (text == null || text.isBlank()) {
            return "";
        }

        // Normalize line endings
        text = text.replace("\r\n", "\n");

        // Remove excessive spaces/tabs
        text = text.replaceAll("[ \\t]+", " ");

        // Reduce excessive blank lines
        text = text.replaceAll("\\n{3,}", "\n\n");

        return text.trim();
    }

    private List<DocumentChunkData> createChunks(
            String text,
            int chunkSize,
            int overlap
    ) {

        List<DocumentChunkData> chunks = new ArrayList<>();

        if (text.isBlank()) {
            return chunks;
        }

        int start = 0;
        int chunkIndex = 0;

        while (start < text.length()) {

            int end = Math.min(
                    start + chunkSize,
                    text.length()
            );

            String chunk = text
                    .substring(start, end)
                    .trim();

            if (!chunk.isBlank()) {
                chunks.add(
                        new DocumentChunkData(
                                chunkIndex,
                                chunk
                        )
                );
            }

            start += chunkSize - overlap;
            chunkIndex++;
        }

        return chunks;
    }
}
