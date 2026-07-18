package com.GitSenseAI.Retriever.VECTOR.dto;

import java.util.Map;

public record SimilarDocument(
        String id,
        String text,
        Double score,
        Map<String, Object> metadata
) {
}