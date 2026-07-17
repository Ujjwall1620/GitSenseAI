package com.GitSenseAI.VECTOR.mapper;

import com.GitSenseAI.EMBEDDING.dto.EmbeddingResult;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Converts Embedding Module output into Spring AI Document objects for storage. */
@Component
public class EmbeddingResultToDocumentMapper {

    public List<Document> toDocuments(List<EmbeddingResult> results) {
        return results.stream().map(this::toDocument).toList();
    }

    private Document toDocument(EmbeddingResult result) {
        Map<String, Object> metadata = new HashMap<>(result.metadata());
        metadata.put("nodeType", result.nodeType());
        metadata.put("nodeId", result.nodeId());

        String documentId = deterministicUuid(result.nodeId());

        return new Document(documentId, result.originalText(), metadata);
    }

    /**
     * Derives a stable UUID from our own node id string, rather than letting
     * Spring AI generate a random one. Same nodeId always produces the same
     * UUID, so re-analyzing an unchanged node correctly upserts the existing
     * vector row instead of creating a duplicate on every run. The original
     * human-readable nodeId is preserved in metadata for lookup/debugging.
     */
    private String deterministicUuid(String nodeId) {
        return UUID.nameUUIDFromBytes(nodeId.getBytes(StandardCharsets.UTF_8)).toString();
    }
}