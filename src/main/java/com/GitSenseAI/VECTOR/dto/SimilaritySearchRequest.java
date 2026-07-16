package com.GitSenseAI.VECTOR.dto;

public record SimilaritySearchRequest(
        String query,
        Integer topK
) {
}