package com.GitSenseAI.RAG.dto;

public record SourceReference(
        String nodeId,
        String nodeType,
        Double relevanceScore
) {
}