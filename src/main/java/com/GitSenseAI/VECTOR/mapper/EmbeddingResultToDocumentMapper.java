package com.GitSenseAI.VECTOR.mapper;

import com.GitSenseAI.EMBEDDING.dto.EmbeddingResult;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Converts Embedding Module output into Spring AI Document objects for storage. */
@Component
public class EmbeddingResultToDocumentMapper {

    public List<Document> toDocuments(List<EmbeddingResult> results) {
        return results.stream().map(this::toDocument).toList();
    }

    private Document toDocument(EmbeddingResult result) {
        Map<String, Object> metadata = new HashMap<>(result.metadata());
        metadata.put("nodeType", result.nodeType());

        return new Document(result.nodeId(), result.originalText(), metadata);
    }
}