package com.GitSenseAI.EMBEDDING.dto;

import java.util.List;
import java.util.Map;

/**
 * A single node's embedding result.
 *
 * @param nodeId       id of the source GraphNode
 * @param nodeType     structural type of the source GraphNode
 * @param originalText the AI-readable text that was embedded
 * @param vector       the resulting embedding vector
 * @param metadata     supplementary information (currently just nodeType, extensible)
 */
public record EmbeddingResult(
        String nodeId,
        String nodeType,
        String originalText,
        List<Float> vector,
        Map<String, String> metadata
) {
}