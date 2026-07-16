package com.GitSenseAI.EMBEDDING.dto;

import java.util.List;

/** API response returned by POST /api/v1/embedding/generate. */
public record EmbeddingResponse(
        int totalNodesEmbedded,
        int embeddingDimensions,
        List<EmbeddingResult> embeddings
) {
}