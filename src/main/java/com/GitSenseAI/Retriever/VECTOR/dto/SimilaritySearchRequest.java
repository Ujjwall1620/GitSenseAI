package com.GitSenseAI.Retriever.VECTOR.dto;

public record SimilaritySearchRequest(
        String query,
        Integer topK
) {
}